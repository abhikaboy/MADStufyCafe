package com.example.composeapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.composeapp.R
import com.example.composeapp.data.network.Review
import com.example.composeapp.ui.theme.CardBackground
import com.example.composeapp.ui.theme.TextPrimary
import com.example.composeapp.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReviewCard(
    review: Review,
    cafeName: String = "Unknown Cafe",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with cafe name and date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cafeName,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = formatDate(review.created_at),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Rating stars
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Overall: ",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )

                repeat(5) { index ->
                    Image(
                        painter = painterResource(
                            id = if (index < review.overall_rating.toInt())
                                R.drawable.filled_star
                            else
                                R.drawable.star_icon
                        ),
                        contentDescription = "Star",
                        modifier = Modifier
                            .size(16.dp)
                            .padding(1.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "(${review.overall_rating}/5.0)",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = "WiFi: ${review.wifi_quality}/5",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )

            Text(
                text = "Outlets: ${review.outlet_accessibility}/5",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )


            // Atmosphere tags if available
            if (!review.atmosphere.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Atmosphere: ${review.atmosphere}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Study friendly tags if available
            if (!review.study_friendly.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Study-friendly: ${review.study_friendly}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

private fun formatDate(dateString: String?): String {
    if (dateString.isNullOrBlank()) return "Recently"

    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        "Recently"
    }
}