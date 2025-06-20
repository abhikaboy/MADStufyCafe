package com.example.composeapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.composeapp.ui.components.SearchBar

@Composable
fun BookMarkScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bookmarks",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Monkey D Luffy's Bookmarks",
            style = MaterialTheme.typography.bodyLarge
        )
        SearchBar()
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewBookMarks() {
    BookMarkScreen()
}