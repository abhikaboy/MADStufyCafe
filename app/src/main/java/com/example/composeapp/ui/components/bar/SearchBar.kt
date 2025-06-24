package com.example.composeapp.ui.components.bar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeapp.R
import com.example.composeapp.ui.theme.Primary
import com.example.composeapp.ui.theme.TextPrimary

@Composable
fun SearchBar(onSearch: (String) -> Unit = {}) {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    
    fun performSearch() {
        if (text.isNotBlank()) {
            onSearch(text.trim())
            focusManager.clearFocus()
        }
    }
    
    OutlinedTextField(
        value = text,
        onValueChange = { 
            text = it
            // Trigger search on text change for live search
            if (it.isBlank()) {
                onSearch("")
            }
        },
        label = { Text("Search for a Cafe Rating") },
        placeholder = { Text("Enter cafe name or location...") },
        shape = RoundedCornerShape(50.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = TextPrimary,
            focusedLabelColor = Primary,
            unfocusedLabelColor = TextPrimary,
        ),
        leadingIcon = {
            Image(
                painter = painterResource(id = R.drawable.search_icon),
                contentDescription = "Search Icon",
                modifier = Modifier.size(26.dp)
            )
        },
        trailingIcon = {
            if (text.isNotBlank()) {
                IconButton(
                    onClick = { performSearch() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Submit Search",
                        tint = Primary
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { 
                performSearch()
            }
        ),
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
fun PreviewSearchBar() {
    SearchBar()
}