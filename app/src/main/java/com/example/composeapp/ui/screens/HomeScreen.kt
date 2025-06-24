package com.example.composeapp.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.ui.components.SearchBar
import com.example.composeapp.R
import com.example.composeapp.data.network.toEntity
import com.example.composeapp.ui.components.CafeList
import com.example.composeapp.ui.components.ExpandableFilterBar
import com.example.composeapp.ui.theme.TextPrimary


@Composable
fun HomeScreen(
    cafeList: List<CafeEntity>,
    onCafeClick: (CafeEntity) -> Unit,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onBookmarkClick: (CafeEntity) -> Unit = {},
    onSearch: (String) -> Unit = {},
    onResume: () -> Unit = {}
) {
    var filteredList by remember(cafeList) { mutableStateOf(cafeList) }
    val selectedLabels = remember { mutableStateOf(mutableSetOf<String>()) }

    // Remove the toEntity() conversion since filteredList is already List<CafeEntity>
    val cafeEntities = remember(filteredList) {
        filteredList
    }

    //Logging to see if cafe has tags
    LaunchedEffect(cafeEntities) {
        cafeEntities.forEach { cafe ->
            Log.d(
                "CafeEntities", "Cafe: ${cafe.name}, " +
                        "Atmosphere: ${cafe.atmosphereTags}, " +
                        "EnergyLevel: ${cafe.energyLevelTags}" +
                        "StudyFriendly: ${cafe.studyFriendlyTags}"
            )
        }
    }
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

    val filteredCafeEntity = remember(selectedLabels.value, cafeEntities) {
        if (selectedLabels.value.isEmpty()) {
            cafeEntities
        } else {
            cafeEntities.filter { cafe ->
                val allTags = listOf(
                    cafe.tags,
                    cafe.atmosphereTags,
                    cafe.energyLevelTags,
                    cafe.studyFriendlyTags
                )
                    .filter { it.isNotBlank() }
                    .flatMap { it.split(",").map { tag -> tag.trim() } }

                selectedLabels.value.any { label ->
                    allTags.any { tag -> tag.equals(label, ignoreCase = true) }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SearchBar(onSearch = onSearch)
        Spacer(modifier = Modifier.height(10.dp))
        ExpandableFilterBar(
            selectedLabels = selectedLabels.value,
            onLabelToggle = { label ->
                selectedLabels.value = selectedLabels.value.toMutableSet().apply {
                    if (!add(label)) remove(label) // Toggle
                }
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECOMMENDED CAFE'S NEAR YOU",
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        CafeList(
            cafeList = filteredCafeEntity, // Use filtered results instead of original cafeList
            onCafeClick = onCafeClick,
            onBookmarkClick = onBookmarkClick,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh
        )
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
            powerOutlets = "Many",
            wifiQuality = "Excellent",
            imageUrl = "",
        ),
        CafeEntity(
            name = "Study Spot",
            address = "45 College Ave",
            tags = "",
            studyRating = 3,
            powerOutlets = "Some",
            wifiQuality = "Good",
            imageUrl = "",
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
    HomeScreen(cafesList, onCafeClick = {})
}