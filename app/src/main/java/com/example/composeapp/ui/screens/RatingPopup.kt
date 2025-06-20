package com.example.composeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeapp.data.database.RatingEntity
import com.example.composeapp.ui.components.RatingPopupInfo
import com.example.composeapp.ui.theme.ComposeAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingPopup(
    rating: RatingEntity?,
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    if (isVisible && rating != null) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = onDismiss,
            dragHandle = {
                Surface(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.Gray.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 32.dp, height = 4.dp)
                    )
                }
            },
            windowInsets = WindowInsets(0)
        ) {
            ComposeAppTheme {
                Box(
                    modifier = Modifier.padding(
                        start = 0.dp,
                        end = 0.dp,
                        bottom = 0.dp
                    )
                ) {
                    RatingPopupInfo(rating = rating)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRatingPopup() {
    val sampleRating = RatingEntity(
        id = 1,
        name = "Caffe Bene",
        address = "14 Massachusetts Ave, Boston, MA",
        studyRating = 3,
        outletInfo = "Some",
        wifiQuality = "Excellent",
        atmosphereTags = listOf("Cozy", "Rustic", "Traditional", "Warm", "Clean"),
        energyLevelTags = listOf("Quiet", "Low-Key", "Tranquil", "Moderate", "Average"),
        studyFriendlyTags = listOf("Study Haven", "Good", "Decent", "Mixed", "Fair"),
        imageUrls = listOf(
            "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=400",
            "https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400",
            "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400",
            "https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=400"
        )
    )

    ComposeAppTheme {
        RatingPopupInfo(rating = sampleRating)
    }
}