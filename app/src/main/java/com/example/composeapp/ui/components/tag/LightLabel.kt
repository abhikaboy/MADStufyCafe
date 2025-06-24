package com.example.composeapp.ui.components.tag

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.composeapp.ui.theme.CardBackground
import com.example.composeapp.ui.theme.TagBackground
import com.example.composeapp.ui.theme.TextPrimary

object LightLabelColors {
    val background = TagBackground
    val selectedBackground = CardBackground
    val text = TextPrimary
}

@Composable
fun LightLabel(
    text: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = if (isSelected) LightLabelColors.selectedBackground else LightLabelColors.background,
    textColor: Color = if (isSelected) LightLabelColors.text else TagColors.text,
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
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}