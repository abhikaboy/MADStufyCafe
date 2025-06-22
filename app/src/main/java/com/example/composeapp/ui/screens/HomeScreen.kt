package com.example.composeapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.ui.components.SearchBar
import com.example.composeapp.R
import com.example.composeapp.ui.components.CafeList

@Composable
fun HomeScreen(cafeList: List<CafeEntity>, onCafeClick: (CafeEntity) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SearchBar()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECOMMENDED CAFE'S NEAR YOU",
                style = MaterialTheme.typography.bodyMedium
            )
            Image(
                painter = painterResource(id = R.drawable.phosphor2),
                contentDescription = "phosphor",
                Modifier.padding(top = 6.dp, bottom = 5.dp)
            )
        }
        CafeList(cafeList, onCafeClick)
    }
}


@Composable
@Preview(
    showBackground = true
)
fun PreviewHomeScreen() {
    val cafesList = listOf(
        CafeEntity(
            name = "Bean & Brew",
            address = "123 Main Street",
            tags = "",
            studyRating = 4,
            outletInfo = "Many",
            wifiQuality = "Excellent",
            imageUrl = "",
        ),
        CafeEntity(
            name = "Study Spot",
            address = "45 College Ave",
            tags = "",
            studyRating = 3,
            outletInfo = "Some",
            wifiQuality = "Good",
            imageUrl = "",
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
    //HomeScreen(cafesList, onCafeClick = {})
}