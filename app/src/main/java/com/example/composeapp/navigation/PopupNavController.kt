package com.example.composeapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.ui.screens.CafePopup
import com.example.composeapp.ui.screens.HomeScreen
import com.example.composeapp.ui.viewmodel.CafeViewModel
import com.example.composeapp.ui.viewmodel.ReviewViewModel
import com.example.composeapp.ui.viewmodel.UserViewModel

@Composable
fun MainContent(
    cafeList: List<CafeEntity>, 
    navController: NavHostController,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onBookmarkClick: (CafeEntity) -> Unit = {},
    onSearch: (String) -> Unit = {},
    reviewViewModel: ReviewViewModel? = null,
    currentUserId: String? = null,
    userViewModel: UserViewModel? = null,
    onResume: () -> Unit = {},
    cafeViewModel: CafeViewModel? = null
) {
    //Created blank cafe since I don't want to deal with null CafeEntity
    val blankCafe = CafeEntity(
        id = 0,
        name = "",
        address = ""
    )
    var selectedCafe by remember { mutableStateOf<CafeEntity>(blankCafe) }
    var isPopupVisible by remember { mutableStateOf(false) }

    HomeScreen(
        cafeList = cafeList,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        onBookmarkClick = onBookmarkClick,
        onSearch = onSearch,
        onResume = onResume,
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
        }
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
            currentUserId = currentUserId,
            cafeViewModel = cafeViewModel
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNavController() {
    val controller = rememberNavController()
    val cafesList = listOf(
        CafeEntity(
            name = "Bean & Brew",
            address = "123 Main Street",
            tags = "",
            studyRating = 4,
            powerOutlets = "Many",
            wifiQuality = "Excellent",
            atmosphereTags = "Cozy,Rustic,Traditional,Warm,Clean",
            energyLevelTags = "Quiet,Low-Key,Tranquil,Moderate,Average",
            studyFriendlyTags = "Study-Haven,Good,Decent,Mixed,Fair",
            imageUrl = ""
        ),
        CafeEntity(
            name = "Study Spot",
            address = "45 College Ave",
            tags = "",
            studyRating = 3,
            powerOutlets = "Some",
            wifiQuality = "Good",
            atmosphereTags = "Cozy,Rustic,Traditional,Warm,Clean",
            energyLevelTags = "Quiet,Low-Key,Tranquil,Moderate,Average",
            studyFriendlyTags = "Study-Haven,Good,Decent,Mixed,Fair",
            imageUrl = ""
        ),
        CafeEntity(
            name = "Java House",
            address = "88 Coffee Blvd",
            tags = "",
            studyRating = 5,
            powerOutlets = "Few",
            wifiQuality = "Fair",
            atmosphereTags = "Cozy,Rustic,Traditional,Warm,Clean",
            energyLevelTags = "Quiet,Low-Key,Tranquil,Moderate,Average",
            studyFriendlyTags = "Study-Haven,Good,Decent,Mixed,Fair",
            imageUrl = ""
        )
    )
    MainContent(cafesList, controller)
}