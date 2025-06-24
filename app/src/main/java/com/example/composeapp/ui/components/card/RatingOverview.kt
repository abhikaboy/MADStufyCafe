package com.example.composeapp.ui.components.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeapp.ui.theme.ComposeAppTheme
import com.example.composeapp.ui.theme.TagBackground
import com.example.composeapp.ui.theme.TextPrimary

@Composable
fun RatingOverviewCard(
    cafesVisited: Int = 23,
    averageRating: Float = 4.3f,
    bookmarks: Int = 14,
    exploredPercentage: Int = 23,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(
            width = 1.dp,
            color = TagBackground
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Rating Overview",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFA88E8E)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$cafesVisited Cafes Visited",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary
                )

                Text(
                    text = "Avg Rating $averageRating",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFA88E8E)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = exploredPercentage / 100f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = TextPrimary,
                trackColor = Color(0xFFD9D9D9)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$bookmarks bookmarks",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFA88E8E)
                )

                Text(
                    text = "$exploredPercentage% explored",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFA88E8E)
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun PreviewRatingOverviewCard() {
    ComposeAppTheme {
        Column {
            RatingOverviewCard()
        }
    }
}
