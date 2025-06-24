package com.example.composeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.composeapp.R
import com.example.composeapp.data.network.Bookmark
import com.example.composeapp.data.network.Cafe
import com.example.composeapp.data.network.Review
import com.example.composeapp.data.network.UserResponse
import com.example.composeapp.ui.components.ReviewCard
import com.example.composeapp.ui.components.RatingOverviewCard
import com.example.composeapp.ui.theme.TextPrimary

@Composable
fun UserProfile(
    currentUser: UserResponse?,
    userReviews: List<Review> = emptyList(),
    userBookmarks: List<Cafe> = emptyList(),
    getCafeName: (String) -> String = { "Unknown Cafe" },
    onReviewClick: (Review) -> Unit = {},
    onResume: () -> Unit = {}
) {
    // Handle onResume lifecycle event
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                onResume()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Profile",
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(20.dp))
        
        // Profile picture placeholder
        Image(
            painterResource(id = R.drawable.luffy),
            contentDescription = "Profile Picture", 
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(100.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(20.dp))

        // User name
        Text(
            text = currentUser?.name ?: "Guest User",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(15.dp))
        
        // User statistics
        RatingOverviewCard(
            cafesVisited = currentUser?.cafes_visited ?: 0,
            averageRating = currentUser?.average_rating?.toFloat() ?: 0f,
            bookmarks = userBookmarks.size, // Show number of reviews instead of bookmarks
            exploredPercentage = calculateExploredPercentage(currentUser?.cafes_visited ?: 0, userBookmarks.size)
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        // Recent reviews section
        Text(
            text = "Recent Reviews",
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        
        if (userReviews.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(userReviews.take(5)) { review -> // Show only 5 recent reviews
                    ReviewCard(
                        review = review,
                        cafeName = getCafeName(review.study_spot_id),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "No reviews yet. Start exploring cafes and leave your first review!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary.copy(alpha = 0.7f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
        
        Spacer(modifier = Modifier.height(15.dp))
    }
}

// Helper function to calculate explored percentage
private fun calculateExploredPercentage(cafesVisited: Int, cafesBookmarked: Int): Int {
    // Assuming there are roughly 100 cafes in the system
    // This could be made dynamic by passing total cafe count
    return ((cafesVisited.toDouble() / cafesBookmarked) * 100).toInt().coerceAtMost(100)
}

@Preview(showBackground = true)
@Composable
fun PreviewUserProfile() {
    val mockUser = UserResponse(
        id = "1",
        name = "John Doe",
        cafes_visited = 15,
        average_rating = 4.2,
        profile_picture = null,
        created_at = null,
        updated_at = null
    )
    
    val mockReviews = listOf(
        Review(
            id = "1",
            study_spot_id = "cafe1",
            user_id = "1",
            overall_rating = 4.5,
            outlet_accessibility = 4.0,
            wifi_quality = 5.0,
            atmosphere = "Cozy, Warm",
            energy_level = "Quiet",
            study_friendly = "Study Haven",
            photos = emptyList(),
            created_at = "2024-01-15T10:30:00Z"
        ),
        Review(
            id = "2",
            study_spot_id = "cafe2",
            user_id = "1",
            overall_rating = 3.5,
            outlet_accessibility = 3.0,
            wifi_quality = 4.0,
            atmosphere = "Modern, Clean",
            energy_level = "Moderate",
            study_friendly = "Good",
            photos = emptyList(),
            created_at = "2024-01-10T14:20:00Z"
        )
    )
    
    UserProfile(
        currentUser = mockUser,
        userReviews = mockReviews,
        getCafeName = { cafeId -> 
            when (cafeId) {
                "cafe1" -> "Bean & Brew"
                "cafe2" -> "Study Spot"
                else -> "Unknown Cafe"
            }
        }
    )
}