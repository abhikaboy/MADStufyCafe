package com.example.composeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeapp.data.database.AppDatabase
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.data.network.NetworkModule
import com.example.composeapp.data.network.ApiResult
import com.example.composeapp.data.repository.CafeRepository
import com.example.composeapp.navigation.MainContent
import com.example.composeapp.navigation.Screen
import com.example.composeapp.ui.components.BottomNavigationBar
import com.example.composeapp.ui.screens.BookMarkScreen
import com.example.composeapp.ui.screens.UserProfile
import com.example.composeapp.ui.theme.*
import com.example.composeapp.ui.viewmodel.CafeViewModel
import com.example.composeapp.ui.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var repository: CafeRepository
    private lateinit var viewModelFactory: ViewModelFactory
    private val cafeViewModel: CafeViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database
        val database = AppDatabase.getDatabase(applicationContext)

        // Initialize repository with both database and API service
        repository = CafeRepository(
            cafeDao = database.cafeDao(),
            apiService = NetworkModule.apiService
        )

        // Initialize ViewModel factory
        viewModelFactory = ViewModelFactory(repository)

        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Background
            ) {
                AppContent(cafeViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(viewModel: CafeViewModel) {
    val mainNavController = rememberNavController()

    // Observe cafes from ViewModel using LiveData
    val cafesResult by viewModel.allCafes.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)
    val isRefreshing by viewModel.isRefreshing.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { _ ->
            NavHost(
                navController = mainNavController,
                startDestination = Screen.Home.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Home.route) {
                    // Handle different states for Home screen
                    when {
                        isLoading && cafesResult == null -> {
                            LoadingScreen()
                        }
                        cafesResult is ApiResult.Error && errorMessage != null -> {
                            ErrorScreen(
                                message = errorMessage ?: "Unknown error",
                                onRetry = { viewModel.refreshCafes() },
                                onDismissError = { viewModel.clearError() }
                            )
                        }
                        cafesResult is ApiResult.Success -> {
                            val cafes = (cafesResult as ApiResult.Success<List<CafeEntity>>).data
                            if (cafes.isEmpty()) {
                                EmptyStateScreen(onRefresh = { viewModel.refreshCafes() })
                            } else {
                                val popupNavController = rememberNavController()
                                MainContent(
                                    cafeList = cafes,
                                    navController = popupNavController,
                                    isRefreshing = isRefreshing,
                                    onRefresh = { viewModel.refreshCafes() },
                                    onBookmarkClick = { cafe ->
                                        viewModel.bookmarkCafe(cafe.id, !cafe.isBookmarked)
                                    },
                                    onSearch = { query -> viewModel.searchCafes(query) }
                                )
                            }
                        }
                        else -> {
                            LoadingScreen()
                        }
                    }
                }

                composable(Screen.Bookmarks.route) {
                    when (cafesResult) {
                        is ApiResult.Success -> {
                            val cafes = (cafesResult as ApiResult.Success<List<CafeEntity>>).data
                            // Filter bookmarked cafes - you'll need to add isBookmarked field to CafeEntity
                            val bookmarkedCafes = cafes.filter { it.isBookmarked }
                            BookMarkScreen(cafeList = bookmarkedCafes)
                        }
                        else -> {
                            BookMarkScreen(cafeList = emptyList())
                        }
                    }
                }

                composable(Screen.Profile.route) {
                    when (cafesResult) {
                        is ApiResult.Success -> {
                            val cafes = (cafesResult as ApiResult.Success<List<CafeEntity>>).data
                            UserProfile(cafeList = cafes)
                        }
                        else -> {
                            UserProfile(cafeList = emptyList())
                        }
                    }
                }
            }
        }

        // Navigation bar as overlay
        BottomNavigationBar(
            navController = mainNavController,
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
    onDismissError: () -> Unit
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
            }
        }
    }
}

@Composable
fun EmptyStateScreen(onRefresh: () -> Unit) {
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
            Button(
                onClick = onRefresh,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary
                )
            ) {
                Text("Refresh")
            }
        }
    }
}