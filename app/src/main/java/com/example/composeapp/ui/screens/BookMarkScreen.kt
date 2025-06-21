package com.example.composeapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import java.nio.file.WatchEvent

@Composable
fun BookMarkScreen(cafeList: List<CafeEntity>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bookmarks",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = "Monkey D Luffy's Bookmarks",
            style = MaterialTheme.typography.titleLarge
        )
        SearchBar()
        Spacer(modifier = Modifier.height(10.dp))
        ExpandableFilterBar()
        Text(
            text = "Showing 14 Bookmarked Cafes",
            modifier = Modifier.align(Alignment.Start)
        )
        CafeList(cafeList)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBookMarks() {
    val cafesList = listOf(
        CafeEntity(
            name = "Bean & Brew",
            address = "123 Main Street",
            tags = "",
            studyRating = 4,
            outletInfo = "Many",
            wifiQuality = "Excellent",
            imageUrl = ""
        ),
        CafeEntity(
            name = "Study Spot",
            address = "45 College Ave",
            tags = "",
            studyRating = 3,
            outletInfo = "Some",
            wifiQuality = "Good",
            imageUrl = ""
        ),
        CafeEntity(
            name = "Java House",
            address = "88 Coffee Blvd",
            tags = "",
            studyRating = 5,
            outletInfo = "Few",
            wifiQuality = "Fair",
            imageUrl = ""
        )
    )
    BookMarkScreen(cafesList)
}