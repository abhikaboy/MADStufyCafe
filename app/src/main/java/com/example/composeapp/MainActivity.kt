package com.example.composeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.composeapp.data.database.AppDatabase
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.data.repository.CafeRepository
import com.example.composeapp.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize database
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = CafeRepository(database.cafeDao())
        
        setContent {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Background
            ) {
                CafeListScreen(repository)
            }
        }
    }
}

@Composable
fun CafeListScreen(repository: CafeRepository) {
    val cafes by repository.allCafes.collectAsState(initial = emptyList())
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Cafes",
                        style = Typography.titleLarge,
                        color = TextPrimary
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Primary,
                    titleContentColor = TextPrimary
                )
            )
        }
    ) { paddingValues ->
        if (cafes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No cafes found",
                    style = Typography.bodyLarge,
                    color = TextSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(cafes) { cafe ->
                    CafeItem(cafe)
                }
            }
        }
    }
}

@Composable
fun CafeItem(cafe: CafeEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Surface
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = cafe.name,
                style = Typography.titleLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = cafe.address,
                style = Typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = cafe.tags,
                style = Typography.labelMedium,
                color = TextSecondary
            )
        }
    }
} 