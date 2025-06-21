package com.example.composeapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeapp.ui.theme.TextPrimary
import com.example.composeapp.ui.theme.ComposeAppTheme
import com.example.composeapp.ui.theme.LargeCardBackground
import com.example.composeapp.ui.theme.TextBackground

@Composable
fun WifiRateButton(
    rating: String,
    text: String,
    onClick: () -> Unit,
    isSelected: Boolean = false,
    enabled: Boolean = true,
    cornerRadius: Int = 12,
    strokeWidth: Float = 0.5f,

    ) {
    val finalBackgroundColor = when {
        isSelected -> TextPrimary
        else -> TextBackground
    }
    val finalTextColor = when {
        isSelected -> Color.White
        else -> TextPrimary
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(cornerRadius.dp),
        color = finalBackgroundColor,
        border = BorderStroke(
            width = strokeWidth.dp,
            color = TextPrimary
        ),
        modifier = Modifier
            .clickable(enabled = enabled) { onClick() }
            .size(width = 50.dp, height = 70.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Surface(
                shape = CircleShape,
                color = when {
                    isSelected -> Color.White.copy(alpha = 0.2f)
                    else -> TextPrimary
                },
                modifier = Modifier.size(30.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = rating,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp,
                        color = when {
                            isSelected -> Color.White
                            else -> LargeCardBackground
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) finalTextColor else finalTextColor.copy(alpha = 0.6f)
            )
        }
    }
}


// Usage example with multiple buttons
@Preview(showBackground = true)
@Composable
fun PreviewWifiRateButtons() {
    ComposeAppTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            WifiRateButton(
                rating = "0",
                text = "None",
                onClick = { },
                isSelected = true
            )
            WifiRateButton(
                rating = "1",
                text = "Poor",
                onClick = { },
                isSelected = false
            )
            WifiRateButton(
                rating = "2",
                text = "Fair",
                onClick = { },
                isSelected = false
            )
            WifiRateButton(
                rating = "3",
                text = "Good",
                onClick = { },
                isSelected = false
            )
        }
    }
}