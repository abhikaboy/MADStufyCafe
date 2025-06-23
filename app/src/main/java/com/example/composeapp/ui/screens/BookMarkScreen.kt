package com.example.composeapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.data.network.Cafe
import com.example.composeapp.data.network.Address
import com.example.composeapp.data.network.Location
import com.example.composeapp.data.network.toEntity
import com.example.composeapp.ui.components.CafeList
import com.example.composeapp.ui.components.ExpandableFilterBar
import com.example.composeapp.ui.components.SearchBar
import com.example.composeapp.ui.theme.TextPrimary

@Composable
fun BookMarkScreen(
    cafeList: List<Cafe>,
    onCafeClick: (CafeEntity) -> Unit = {},
    onBookmarkClick: (CafeEntity) -> Unit = {},
    onSearch: (String) -> Unit = {},
    onResume: () -> Unit = {}
) {
    val context = LocalContext.current
    var filteredList by remember(cafeList) { mutableStateOf(cafeList) }
    
    // Convert Cafe objects to CafeEntity for display
    val cafeEntities = remember(filteredList) {
        filteredList.map { it.toEntity() }
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
            text = "Your Bookmarked Cafes",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(10.dp))
        SearchBar(
            onSearch = { query ->
                filteredList = if (query.isBlank()) {
                    cafeList
                } else {
                    cafeList.filter { cafe ->
                        val fullAddress = cafe.address?.let { 
                            "${it.street}, ${it.city}, ${it.state} ${it.zip_code}" 
                        } ?: "Address not available"
                        val cafeName = cafe.name ?: "Unknown Cafe"
                        cafeName.contains(query, ignoreCase = true) ||
                        fullAddress.contains(query, ignoreCase = true)
                    }
                }
                onSearch(query)
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        ExpandableFilterBar()
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Showing ${filteredList.size} bookmarked cafes",
            style = MaterialTheme.typography.bodyMedium,
            color = TextPrimary,
            modifier = Modifier.align(Alignment.Start)
        )
        CafeList(
            cafeList = cafeEntities,
            onCafeClick = onCafeClick,
            onBookmarkClick = { cafe ->
                onBookmarkClick(cafe)
                // Show toast when bookmark action is performed
                Toast.makeText(context, "Bookmark updated!", Toast.LENGTH_SHORT).show()
            },
            isRefreshing = false,
            onRefresh = onResume
        )
        Spacer(modifier = Modifier.height(15.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBookMarkScreen() {
    val cafesList = listOf(
        Cafe(
            id = "1",
            name = "Bean & Brew",
            address = Address(
                street = "123 Main Street",
                city = "Boston",
                state = "MA",
                zip_code = "02101"
            ),
            location = Location(coordinates = listOf(-71.0589, 42.3601)),
            average_rating = 4,
            outlet_accessibility = 3,
            wifi_access = 3
        ),
        Cafe(
            id = "2",
            name = "Study Spot",
            address = Address(
                street = "45 College Ave",
                city = "Cambridge",
                state = "MA",
                zip_code = "02139"
            ),
            location = Location(coordinates = listOf(-71.1097, 42.3736)),
            average_rating = 3,
            outlet_accessibility = 2,
            wifi_access = 2
        )
    )
    BookMarkScreen(cafesList)
}