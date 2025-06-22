package com.example.composeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeapp.R
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.ui.components.CafeList
import com.example.composeapp.ui.components.RatingOverviewCard
import com.example.composeapp.ui.theme.TextPrimary

@Composable
fun UserProfile(cafeList: List<CafeEntity>) {
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
        Image(
            painterResource(id = R.drawable.cafe), "Card Image", Modifier
                .width(100.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(100.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Monkey D Luffy",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(15.dp))
        RatingOverviewCard(
            cafesVisited = cafeList.size,
            averageRating = cafeList.map { it.studyRating }.average().toFloat(),
            bookmarks = cafeList.size,
            exploredPercentage = 23
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Recent Reviews",
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            modifier = Modifier.align(Alignment.Start)
        )
        CafeList(
            cafeList = cafeList,
            onCafeClick = { cafe -> /* Handle cafe click */ },
            onBookmarkClick = { cafe -> /* Handle bookmark */ },
            isRefreshing = false,
            onRefresh = { /* Handle refresh */ }
        )
        //CafeList(cafeList, onNavigate = {})
        Spacer(modifier = Modifier.height(15.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserProfile() {
    val cafesList = listOf(
        CafeEntity(
            name = "Bean & Brew",
            address = "123 Main Street",
            tags = "",
            studyRating = 4,
            powerOutlets = "Many",
            wifiQuality = "Excellent",
            imageUrl = ""
        ),
        CafeEntity(
            name = "Study Spot",
            address = "45 College Ave",
            tags = "",
            studyRating = 3,
            powerOutlets = "Some",
            wifiQuality = "Good",
            imageUrl = ""
        ),
        CafeEntity(
            name = "Java House",
            address = "88 Coffee Blvd",
            tags = "",
            studyRating = 5,
            powerOutlets = "Few",
            wifiQuality = "Fair",
            imageUrl = ""
        )
    )
    UserProfile(cafesList)
}