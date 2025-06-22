package com.example.composeapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.composeapp.ui.screens.*
import com.example.composeapp.ui.components.BottomNavigationBar
import com.example.composeapp.navigation.Screen
import com.example.composeapp.ui.theme.ComposeAppTheme
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.data.repository.CafeRepository
import com.example.composeapp.navigation.MainContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafeAppWithDatabase(
    repository: CafeRepository,
    mainNavController: NavHostController = rememberNavController()
) {
    val cafes by repository.allCafes.collectAsState(initial = emptyList())

    LaunchedEffect(cafes) {
        if (cafes.isEmpty()) {
            val sampleCafes = listOf(
                CafeEntity(
                    name = "Bean & Brew",
                    address = "123 Main Street, Boston, MA",
                    studyRating = 4,
                    powerOutlets = "Many",
                    wifiQuality = "Excellent",
                    atmosphereTags = "Cozy,Rustic,Traditional,Warm,Clean",
                    energyLevelTags = "Quiet,Low-Key,Tranquil,Moderate,Average",
                    studyFriendlyTags = "Study-Haven,Good,Decent,Mixed,Fair",
                    ratingImageUrls = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=400"
                ),
                CafeEntity(
                    name = "Study Spot Cafe",
                    address = "45 College Ave, Cambridge, MA",
                    studyRating = 5,
                    powerOutlets = "Some",
                    wifiQuality = "Good",
                    atmosphereTags = "Modern,Clean,Minimalist,Bright",
                    energyLevelTags = "Calm,Focused,Productive",
                    studyFriendlyTags = "Study-Haven,Excellent,Quiet",
                    ratingImageUrls = "https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400"
                ),
                CafeEntity(
                    name = "Java House",
                    address = "88 Coffee Blvd, Somerville, MA",
                    studyRating = 3,
                    powerOutlets = "Few",
                    wifiQuality = "Fair",
                    atmosphereTags = "Cozy,Traditional,Warm",
                    energyLevelTags = "Moderate,Average,Social",
                    studyFriendlyTags = "Mixed,Fair,Decent",
                    ratingImageUrls = "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400"
                )
            )
            repository.insertCafes(sampleCafes)
        }
    }

    ComposeAppTheme {
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
                        val popupNavController = rememberNavController()
                        MainContent(
                            cafeList = cafes,
                            navController = popupNavController
                        )
                    }

                    composable(Screen.Bookmarks.route) {
                        BookMarkScreen(cafeList = cafes)
                    }

                    composable(Screen.Profile.route) {
                        UserProfile(cafeList = cafes)
                    }
                }
            }

            BottomNavigationBar(
                navController = mainNavController,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
            )
        }
    }
}