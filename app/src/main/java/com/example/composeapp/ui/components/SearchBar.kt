package com.example.composeapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeapp.R

@Composable
fun SearchBar() {
    var text by remember { mutableStateOf("") }
    TextField(
        value = text,
        onValueChange = { text = it },
        label = { Text("Search for a Cafe Rating") },
        shape = RoundedCornerShape(25.dp),
        leadingIcon = {
            Image(
                painter = painterResource(id = R.drawable.search_icon),
                contentDescription = "Search Icon",
            )
        }
    )
}

@Preview
@Composable
fun PreviewSerachBar() {
    SearchBar()
}