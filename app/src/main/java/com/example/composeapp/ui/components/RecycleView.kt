package com.example.composeapp.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.example.composeapp.data.database.CafeEntity
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CafeList(cafeList: List<CafeEntity>) {
    LazyColumn() {
        items(cafeList) { cafe ->
            CafeCard(cafe)
            Spacer(modifier = Modifier.padding(bottom = 15.dp))
        }
    }
}

@Composable
@Preview
fun PreviewRecycleView() {
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
    CafeList(cafesList)
}