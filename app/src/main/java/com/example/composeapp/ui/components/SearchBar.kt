package com.example.composeapp.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar() {
    var text by remember { mutableStateOf("") }
    TextField(
        value = text,
        onValueChange = {text = it},
        label = { Text("Search for a Cafe Rating")},
        shape = RoundedCornerShape(25.dp),
    )
}

@Preview
@Composable
fun PreviewSerachBar() {
    SearchBar()
}