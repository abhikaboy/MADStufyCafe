package com.example.composeapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.ui.components.CafeList
import com.example.composeapp.ui.components.ExpandableFilterBar
import com.example.composeapp.ui.components.SearchBar
import com.example.composeapp.ui.theme.TextPrimary

@Composable
fun BookMarkScreen(cafeList: List<CafeEntity>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Bookmarks",
            style = MaterialTheme.typography.titleSmall,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Monkey D Luffy's Bookmarks",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))
        SearchBar()
        Spacer(modifier = Modifier.height(10.dp))
        ExpandableFilterBar()
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Showing ${cafeList.size} bookmarked cafes",
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
fun PreviewBookMarkScreen() {
    val cafesList = listOf(
        CafeEntity(
            name = "Bean & Brew",
            address = "123 Main Street, Boston, MA",
            studyRating = 4,
            powerOutlets = "Many",
            wifiQuality = "Excellent",
            atmosphereTags = "Cozy,Rustic,Traditional,Warm,Clean",
            energyLevelTags = "Quiet,Low-Key,Tranquil,Moderate,Average",
            studyFriendlyTags = "Study-Haven,Good,Decent,Mixed,Fair",
            imageUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=400",
            ratingImageUrls = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=400,https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400,https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400"
        ),
        CafeEntity(
            name = "Study Spot",
            address = "45 College Ave, Cambridge, MA",
            studyRating = 3,
            powerOutlets = "Some",
            wifiQuality = "Good",
            atmosphereTags = "Modern,Clean,Minimalist,Bright",
            energyLevelTags = "Calm,Focused,Productive,Moderate",
            studyFriendlyTags = "Study-Haven,Good,Decent,Quiet",
            imageUrl = "https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400",
            ratingImageUrls = "https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400,https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=400"
        ),
        CafeEntity(
            name = "Java House",
            address = "88 Coffee Blvd, Somerville, MA",
            studyRating = 5,
            powerOutlets = "Few",
            wifiQuality = "Fair",
            atmosphereTags = "Cozy,Traditional,Warm,Industrial",
            energyLevelTags = "Moderate,Average,Social,Lively",
            studyFriendlyTags = "Mixed,Fair,Decent,Social",
            imageUrl = "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400",
            ratingImageUrls = "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400,https://images.unsplash.com/photo-1559056199-641a0ac8b55e?w=400"
        )
    )
    BookMarkScreen(cafesList)
}