package com.example.composeapp.ui.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.composeapp.data.database.CafeEntity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
//
//@Composable
//fun AppNavHost(navController: NavHostController) {
//    val cafesList = listOf(
//        CafeEntity(
//            id = 0,
//            name = "Bean & Brew",
//            address = "123 Main Street",
//            tags = "",
//            studyRating = 4,
//            outletInfo = "Many",
//            wifiQuality = "Excellent",
//            imageUrl = ""
//        )
//    )
//    NavHost(navController = navController, startDestination = "home") {
//        composable("home") {
//            HomeScreen(
//                cafeList = cafesList,
//                onNavigate = { route -> navController.navigate(route) }
//            )
//        }
//        composable("ratingPopup/{cafeId}") { backStackEntry ->
//            val cafeId = backStackEntry.arguments?.getString("cafeId")?.toLongOrNull()
//            val cafe = cafesList.firstOrNull { it.id == cafeId }
//            var isVisible by remember { mutableStateOf(true) }
//
//            if (cafe != null) {
//                RatingPopup(
//                    cafe = cafe,
//                    isVisible = isVisible,
//                    onDismiss = {
//                        isVisible = false
//                    }
//                )
//            } else {
//                Text("Cafe not found")
//            }
//        }
//    }
//}
//
//@Composable
//@Preview(showBackground = true)
//fun PreviewNavController() {
//    val controller = rememberNavController()
//    AppNavHost(controller)
//}
