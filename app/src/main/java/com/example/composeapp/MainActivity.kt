package com.example.composeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeapp.data.database.AppDatabase
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.data.network.NetworkModule
import com.example.composeapp.data.network.ApiResult
import com.example.composeapp.data.network.Review
import com.example.composeapp.data.network.Cafe
import com.example.composeapp.data.repository.CafeRepository
import com.example.composeapp.data.repository.UserRepository
import com.example.composeapp.data.repository.ReviewRepository
import com.example.composeapp.navigation.Screen
import com.example.composeapp.navigation.MainContent
import com.example.composeapp.ui.components.BottomNavigationBar
import com.example.composeapp.ui.screens.LoginScreen
import com.example.composeapp.ui.screens.HomeScreen
import com.example.composeapp.ui.screens.BookMarkScreen
import com.example.composeapp.ui.screens.CafePopup
import com.example.composeapp.ui.screens.UserProfile
import com.example.composeapp.ui.theme.*
import com.example.composeapp.ui.viewmodel.CafeViewModel
import com.example.composeapp.ui.viewmodel.LoginViewModel
import com.example.composeapp.ui.viewmodel.ReviewViewModel
import com.example.composeapp.ui.viewmodel.UserViewModel
import com.example.composeapp.ui.viewmodel.ViewModelFactory
import com.example.composeapp.utils.LocationHelper
import kotlinx.coroutines.delay
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager

class MainActivity : ComponentActivity() {
    
    private lateinit var cafeRepository: CafeRepository
    private lateinit var userRepository: UserRepository
    private lateinit var reviewRepository: ReviewRepository
    private lateinit var viewModelFactory: ViewModelFactory
    private val cafeViewModel: CafeViewModel by viewModels { viewModelFactory }
    private val loginViewModel: LoginViewModel by viewModels { viewModelFactory }
    private val reviewViewModel: ReviewViewModel by viewModels { viewModelFactory }
    private val userViewModel: UserViewModel by viewModels { viewModelFactory }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request location permissions if not granted
        requestLocationPermissions()
        
        // Initialize database
        val database = AppDatabase.getDatabase(applicationContext)
        
        // Initialize location helper
        val locationHelper = LocationHelper(applicationContext)
        
        // Initialize repositories
        cafeRepository = CafeRepository(
            cafeDao = database.cafeDao(),
            apiService = NetworkModule.apiService,
            locationHelper = locationHelper
        )
        
        userRepository = UserRepository(
            apiService = NetworkModule.apiService
        )
        
        reviewRepository = ReviewRepository(
            apiService = NetworkModule.apiService
        )
        
        // Initialize ViewModel factory with all repositories
        viewModelFactory = ViewModelFactory(cafeRepository, userRepository, reviewRepository)
        
        setContent {
            ComposeAppTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Background
            ) {
                    AppNavigation(loginViewModel, cafeViewModel, reviewViewModel, userViewModel)
                }
            }
        }
    }
    
    private fun requestLocationPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        
        // Add photo permissions for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        
        val permissionsNeeded = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        
        if (permissionsNeeded.isNotEmpty()) {
            android.util.Log.d("MainActivity", "Requesting location permissions: $permissionsNeeded")
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), 1001)
        } else {
            android.util.Log.d("MainActivity", "Location permissions already granted")
        }
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        if (requestCode == 1001) {
            val granted = grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            android.util.Log.d("MainActivity", "Location permissions result: granted=$granted")
            
            if (granted) {
                android.util.Log.d("MainActivity", "Location permissions granted, app can now use location services")
            } else {
                android.util.Log.d("MainActivity", "Location permissions denied, app will use default location")
            }
        }
    }
}

@Composable
fun AppNavigation(
    loginViewModel: LoginViewModel,
    cafeViewModel: CafeViewModel,
    reviewViewModel: ReviewViewModel,
    userViewModel: UserViewModel
) {
    val isLoggedIn by loginViewModel.isLoggedIn.observeAsState(false)
    val currentUser by loginViewModel.currentUser.observeAsState()
    
    if (isLoggedIn && currentUser != null) {
        // User is logged in, show main app content
        MainAppContent(cafeViewModel, loginViewModel, reviewViewModel, userViewModel)
    } else {
        // User is not logged in, show login screen
        LoginScreen(
            loginViewModel = loginViewModel,
            onLoginSuccess = {
                // Navigation handled by state observation
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(viewModel: CafeViewModel, loginViewModel: LoginViewModel, reviewViewModel: ReviewViewModel, userViewModel: UserViewModel) {
    val navController = rememberNavController()
    val currentUser by loginViewModel.currentUser.observeAsState()
    
    // State for welcome message visibility
    var showWelcomeMessage by remember { mutableStateOf(true) }
    
    // Hide welcome message after 10 seconds
    LaunchedEffect(Unit) {
        delay(10000) // 10 seconds
        showWelcomeMessage = false
    }
    
    // Observe cafes from ViewModel using LiveData
    val cafesResult by viewModel.allCafes.observeAsState()
    val searchResults by viewModel.searchResults.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val isRefreshing by viewModel.isRefreshing.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()
    
    // Observe user reviews and bookmarks
    val userReviews by userViewModel.userReviews.observeAsState()
    val userBookmarks by userViewModel.userBookmarks.observeAsState()
    
    // Fetch user reviews and bookmarks when user is available
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { userId ->
            userViewModel.getUserReviews(userId)
            userViewModel.loadUserBookmarks(userId)
        }
    }
    
    // State to track if we're showing search results or all cafes
    var isSearchActive by remember { mutableStateOf(false) }
    val displayedCafes = if (isSearchActive) searchResults else cafesResult
    
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Background,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize()) {
                // Welcome message that disappears after 10 seconds
                if (showWelcomeMessage) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LargeCardBackground),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Welcome back!",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary
                                )
                                Text(
                                    text = currentUser?.name ?: "User",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                            Row {
                                OutlinedButton(
                                    onClick = { showWelcomeMessage = false }
                                ) {
                                    Text("Dismiss")
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedButton(
                                    onClick = { loginViewModel.logout() }
                                ) {
                                    Text("Logout")
                                }
                            }
                        }
                    }
                }
                
                // Main navigation content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(paddingValues)
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                    ) {
                        composable(Screen.Home.route) {
                            when {
                                isLoading && displayedCafes == null -> {
                                    LoadingScreen()
                                }
                                displayedCafes is ApiResult.Error && errorMessage != null -> {
                                    ErrorScreen(
                                        message = errorMessage ?: "Unknown error",
                                        onRetry = { 
                                            if (isSearchActive) {
                                                // Re-run the last search
                                                viewModel.searchCafes(viewModel.searchResults.value?.let { "" } ?: "")
                                            } else {
                                                viewModel.refreshCafes()
                                            }
                                        },
                                        onDismissError = { viewModel.clearError() },
                                        onLogout = { loginViewModel.logout() }
                                    )
                                }
                                displayedCafes is ApiResult.Success -> {
                                    val cafes = displayedCafes.data
        if (cafes.isEmpty()) {
                                        EmptyStateScreen(
                                            onRefresh = { 
                                                if (isSearchActive) {
                                                    isSearchActive = false
                                                    viewModel.clearSearch()
                                                }
                                                viewModel.refreshCafes() 
                                            },
                                            onLogout = { loginViewModel.logout() }
                                        )
                                    } else {
                                        // Create a separate NavController for popup navigation
                                        val popupNavController = rememberNavController()
                                        MainContent(
                                            cafeList = cafes,
                                            navController = popupNavController,
                                            isRefreshing = isRefreshing,
                                            onRefresh = { viewModel.refreshCafes() },
                                            onBookmarkClick = { cafe -> 
                                                viewModel.bookmarkCafe(cafe.id, !cafe.isBookmarked)
                                            },
                                            onSearch = { query -> 
                                                if (query.isNotBlank()) {
                                                    isSearchActive = true
                                                    viewModel.searchCafes(query)
                                                } else {
                                                    isSearchActive = false
                                                    viewModel.clearSearch()
                                                }
                                            },
                                            reviewViewModel = reviewViewModel,
                                            currentUserId = currentUser?.id,
                                            userViewModel = userViewModel,
                                            onResume = {
                                                // Refresh cafes when home screen resumes
                                                viewModel.refreshCafes()
                                            }
                                        )
                                    }
                                }
                                else -> {
                                    LoadingScreen()
                                }
                            }
                        }
                        
                        composable(Screen.Bookmarks.route) {
                            when (userBookmarks) {
                                is ApiResult.Success -> {
                                    val bookmarkedCafes = (userBookmarks as ApiResult.Success<List<Cafe>>).data
                                    // Create a separate NavController for popup navigation
                                    val popupNavController = rememberNavController()
                                    BookmarkContent(
                                        cafeList = bookmarkedCafes,
                                        navController = popupNavController,
                                        onBookmarkClick = { cafe -> 
                                            currentUser?.id?.let { userId ->
                                                userViewModel.deleteBookmark(userId, cafe.apiId ?: cafe.id.toString())
                                            }
                                        },
                                        onSearch = { query -> 
                                            // Search is handled within BookMarkScreen now
                                        },
                                        onResume = {
                                            // Refresh bookmarks when screen resumes
                                            currentUser?.id?.let { userId ->
                                                userViewModel.loadUserBookmarks(userId)
                                            }
                                        },
                                        reviewViewModel = reviewViewModel,
                                        currentUserId = currentUser?.id,
                                        userViewModel = userViewModel
                                    )
                                }
                                else -> {
                                    BookMarkScreen(
                                        cafeList = emptyList(),
                                        onResume = {
                                            currentUser?.id?.let { userId ->
                                                userViewModel.loadUserBookmarks(userId)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        
                        composable(Screen.Profile.route) {
                            val reviewsData = when (val reviews = userReviews) {
                                is ApiResult.Success -> reviews.data
                                else -> emptyList()
                            }
                            
                            UserProfile(
                                currentUser = currentUser,
                                userReviews = reviewsData,
                                getCafeName = { cafeId ->
                                    // Try to find cafe name from current cafe list
                                    when (displayedCafes) {
                                        is ApiResult.Success -> {
                                            (displayedCafes as ApiResult.Success<List<CafeEntity>>).data.find { it.apiId == cafeId }?.name ?: "Unknown Cafe"
                                        }
                                        else -> "Unknown Cafe"
                                    }
                                },
                                onReviewClick = { review -> 
                                    // TODO: Add review detail functionality if needed
                                },
                                onResume = {
                                    // Refresh user reviews when profile screen resumes
                                    currentUser?.id?.let { userId ->
                                        userViewModel.getUserReviews(userId)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
        
        // Bottom Navigation Bar as overlay
        BottomNavigationBar(
            navController = navController,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
        )
    }
    
    // Handle error messages with Snackbar
    errorMessage?.let { message ->
        LaunchedEffect(message) {
            // You can show a snackbar here if needed
            // For now, we'll clear the error after showing it
            viewModel.clearError()
            }
        }
    }

@Composable
fun BookmarkContent(
    cafeList: List<Cafe>,
    navController: NavHostController,
    onBookmarkClick: (CafeEntity) -> Unit = {},
    onSearch: (String) -> Unit = {},
    onResume: () -> Unit = {},
    reviewViewModel: ReviewViewModel? = null,
    currentUserId: String? = null,
    userViewModel: UserViewModel? = null
) {
    // Created blank cafe since I don't want to deal with null CafeEntity
    val blankCafe = CafeEntity(
        id = 0,
        name = "",
        address = ""
    )
    var selectedCafe by remember { mutableStateOf<CafeEntity>(blankCafe) }
    var isPopupVisible by remember { mutableStateOf(false) }

    BookMarkScreen(
        cafeList = cafeList,
        onCafeClick = { cafe ->
            // Reset all previous review state before starting new review
            reviewViewModel?.resetAllStates()
            selectedCafe = cafe
            isPopupVisible = true
            // Initialize review for this cafe and user
            reviewViewModel?.startReview(
                cafeId = cafe.apiId,
                userId = currentUserId ?: ""
            )
        },
        onBookmarkClick = onBookmarkClick,
        onSearch = onSearch,
        onResume = onResume
    )

    if (isPopupVisible && selectedCafe != blankCafe) {
        CafePopup(
            cafe = selectedCafe,
            isVisible = isPopupVisible,
            onDismiss = {
                isPopupVisible = false
                selectedCafe = blankCafe
                // Reset all review state when closing popup
                reviewViewModel?.resetAllStates()
            },
            navController = navController,
            reviewViewModel = reviewViewModel,
            onBookmarkClick = onBookmarkClick,
            userViewModel = userViewModel,
            currentUserId = currentUserId
        )
    }
}

@Composable
fun LoadingScreen() {
        Box(
            modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
        ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = Primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading cafes...",
                style = Typography.bodyLarge,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    onDismissError: () -> Unit,
    onLogout: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Oops! Something went wrong",
                style = Typography.headlineSmall,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = Typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                OutlinedButton(
                    onClick = onDismissError
                ) {
                    Text("Dismiss")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary
                    )
                ) {
                    Text("Retry")
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = onLogout
                ) {
                    Text("Logout")
                }
            }
        }
    }
}

@Composable
fun EmptyStateScreen(
    onRefresh: () -> Unit,
    onLogout: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "No cafes found",
                style = Typography.headlineSmall,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Try refreshing to load cafes from the server",
                style = Typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                Button(
                    onClick = onRefresh,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary
                    )
                ) {
                    Text("Refresh")
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = onLogout
                ) {
                    Text("Logout")
                }
            }
        }
    }
} 