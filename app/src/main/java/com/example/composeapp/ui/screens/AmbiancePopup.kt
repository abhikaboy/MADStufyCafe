package com.example.composeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeapp.ui.components.AmbiancePopupInfo
import com.example.composeapp.ui.theme.ComposeAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbiancePopup(
    isVisible: Boolean,
    onDismiss: () -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

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
                //AmbiancePopupInfo()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewAmbiancePopup() {
    ComposeAppTheme {
        //AmbiancePopupInfo()
    }
}