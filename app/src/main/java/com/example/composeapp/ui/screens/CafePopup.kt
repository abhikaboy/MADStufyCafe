package com.example.composeapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.ui.components.popup.AmbiancePopupInfo
import com.example.composeapp.ui.components.popup.ExperiencePopupInfo
import com.example.composeapp.ui.components.popup.RatingPopupInfo
import com.example.composeapp.ui.components.popup.UploadPhotoInfo
import com.example.composeapp.ui.theme.CardBackground
import com.example.composeapp.ui.viewmodel.CafeViewModel
import com.example.composeapp.ui.viewmodel.ReviewViewModel
import com.example.composeapp.ui.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafePopup(
    cafe: CafeEntity,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    navController: NavHostController,
    reviewViewModel: ReviewViewModel? = null,
    onBookmarkClick: ((CafeEntity) -> Unit)? = null,
    userViewModel: UserViewModel? = null,
    currentUserId: String? = null,
    cafeViewModel: CafeViewModel? = null
) {
    val context = LocalContext.current
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    
    // Observe review states
    val reviewCreated = reviewViewModel?.reviewCreated?.observeAsState()?.value
    val photoUploadSuccess = reviewViewModel?.photoUploadSuccess?.observeAsState()?.value
    
    // Show toast when review is created
    LaunchedEffect(reviewCreated) {
        reviewCreated?.let {
            Toast.makeText(context, "Review submitted successfully!", Toast.LENGTH_SHORT).show()
        }
    }
    
    // Reset navigation when popup opens or closes, or when cafe changes
    LaunchedEffect(isVisible, cafe.id) {
        if (isVisible) {
            // Reset to initial screen when popup opens or cafe changes
            navController.navigate("stats") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        } else {
            // Also reset when popup closes (for safety)
            navController.navigate("stats") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }
    
    if (isVisible) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = onDismiss,
            containerColor = CardBackground,
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.Gray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 40.dp, height = 4.dp)
                    )
                }
            },
            windowInsets = WindowInsets(0)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 900.dp)  // <-- limit max height here
            ) {
                // NavHost inside the sheet
                NavHost(
                    navController = navController,
                    startDestination = "stats"
                ) {
                    composable("stats") {
                        RatingPopupInfo(
                            cafe = cafe,
                            reviewViewModel = reviewViewModel,
                            onNext = {
                                navController.navigate("experience")
                            },
                            onBookmarkClick = onBookmarkClick,
                            userViewModel = userViewModel,
                            currentUserId = currentUserId,
                            cafeViewModel = cafeViewModel
                        )
                    }
                    composable("experience") {
                        ExperiencePopupInfo(
                            cafe = cafe,
                            reviewViewModel = reviewViewModel,
                            onBack = {
                                navController.popBackStack()
                            },
                            toShareMoreDetails = {
                                navController.navigate("ambiance")
                            }
                        )
                    }
                    composable("ambiance") {
                        AmbiancePopupInfo(
                            cafe = cafe,
                            reviewViewModel = reviewViewModel,
                            onBack = {
                                navController.popBackStack()
                            },
                            toUploadPhoto = {
                                // Submit the review first
                                reviewViewModel?.submitReview()
                            }
                        )
                        
                        // Navigate to upload screen when review is successfully created
                        LaunchedEffect(reviewCreated) {
                            reviewCreated?.let { review ->
                                // Only navigate if we have a valid review with ID
                                if (review.id.isNotBlank()) {
                                    navController.navigate("upload")
                                }
                            }
                        }
                    }
                    composable("upload") {
                        UploadPhotoInfo(
                            cafe = cafe,
                            reviewViewModel = reviewViewModel,
                            onBack = {
                                navController.popBackStack()
                            },
                            completeReview = {
                                // Complete the review process and close popup
                                onDismiss()
                            }
                        )
                        
                        // Auto-close popup when photo upload is successful
                        LaunchedEffect(photoUploadSuccess) {
                            if (photoUploadSuccess == true) {
                                Toast.makeText(context, "Photo uploaded successfully!", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            }
                        }
                    }
                }
            }
        }
//        ComposeAppTheme {
//            Box(
//                modifier = Modifier.padding(
//                    start = 0.dp,
//                    end = 0.dp,
//                    bottom = 0.dp
//                )
//            ) {
//                AmbiancePopupInfo()
//            }
//        }
    }
}