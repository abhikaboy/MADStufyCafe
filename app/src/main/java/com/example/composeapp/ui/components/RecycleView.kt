package com.example.composeapp.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import com.example.composeapp.data.database.CafeEntity
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun CafeList(cafeList: List<CafeEntity>, onCafeClick: (CafeEntity) -> Unit) {
    LazyColumn() {
        items(cafeList) { cafe ->
            CafeCard(cafe, onClick = { onCafeClick(cafe) })
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
    //CafeList(cafesList)
}