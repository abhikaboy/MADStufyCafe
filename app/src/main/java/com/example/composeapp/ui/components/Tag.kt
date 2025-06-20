package com.example.composeapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.composeapp.ui.theme.TextBackground
import com.example.composeapp.ui.theme.TextPrimary

object TagColors {
    val background = TextBackground
    val border = TextPrimary
    val text = TextPrimary
    val selectedBackground = TextPrimary
    val selectedText = Color.White
}

@Composable
fun Tag(
    text: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = if (isSelected) TagColors.selectedBackground else TagColors.background,
    textColor: Color = if (isSelected) TagColors.selectedText else TagColors.text,
    borderColor: Color = TagColors.border,
    cornerRadius: Int = 8
) {
    val clickableModifier = if (onClick != null) {
        modifier.clickable { onClick() }
    } else {
        modifier
    }

    Surface(
        modifier = clickableModifier,
        shape = RoundedCornerShape(cornerRadius.dp),
        color = backgroundColor,
        border = BorderStroke(0.5.dp, borderColor)
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
        )
    }
}