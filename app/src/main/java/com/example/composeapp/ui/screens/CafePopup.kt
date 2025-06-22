package com.example.composeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CafePopup(
    cafe: CafeEntity,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    navController: NavHostController
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
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
                            onNext = {
                                navController.navigate("experience")
                            }
                        )
                    }
                    composable("experience") {
                        ExperiencePopupInfo(
                            cafe = cafe,
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
                            onBack = {
                                navController.popBackStack()
                            },
                            toUploadPhoto = {
                                navController.navigate("upload")
                            }
                        )
                    }
                    composable("upload") {
                        UploadPhotoInfo(
                            cafe = cafe,
                            onBack = {
                                navController.popBackStack()
                            },
                            completeReview = {
                            }
                        )
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