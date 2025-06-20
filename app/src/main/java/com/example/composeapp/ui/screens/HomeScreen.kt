package com.example.composeapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.composeapp.ui.components.SearchBar

@Composable
fun HomeScreen() {
    Column() {
        SearchBar()
        Row() {
            Text(
                text = "RECOMMENDED CAFE'S NEAR YOU"
            )
        }
    }
}


@Composable
@Preview
fun Preview() {
    HomeScreen()
}