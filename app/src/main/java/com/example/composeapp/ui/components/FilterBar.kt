package com.example.composeapp.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeapp.R
import androidx.compose.ui.draw.clip
import com.example.composeapp.ui.theme.CardBackground
import com.example.composeapp.ui.theme.LargeCardBackground
import androidx.compose.foundation.layout.FlowRow
import com.example.composeapp.ui.theme.TextPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExpandableFilterBar() {
    var expanded by remember { mutableStateOf(false) }
    var selectedLabels by remember { mutableStateOf(setOf<String>()) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color = CardBackground)
            .border(2.dp, TextPrimary, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 3.dp)
            )
            Icon(
                painter = painterResource(id = if (expanded) R.drawable.star_icon else R.drawable.filled_star),
                contentDescription = "Expand",
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }

        // Expandable content
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                modifier = Modifier.padding(top = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 5
            ) {
                listOf("Cozy", "Rustic", "Traditional", "Warm",
                    "Clean", "Quiet", "Low-Key", "Tranquil", "Moderate", "Average",
                    "Study Haven", "Good", "Decent", "Mixed", "Fair").forEach { label ->
                    FilterChip(
                        text = label,
                        isSelected = selectedLabels.contains(label),
                        onClick = { selectedLabels = if (selectedLabels.contains(label)) {
                            selectedLabels - label // remove from selection
                        } else {
                            selectedLabels + label // add to selection
                        }}
                    )
                }
            }
        }
    }
}

@Composable
fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        color = TextPrimary,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else LargeCardBackground,
                shape = RoundedCornerShape(40.dp)
            )
            .border(0.5.dp, TextPrimary, shape = RoundedCornerShape(50.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

@Preview()
@Composable
fun PreviewExpandableFilterMenu() {
    ExpandableFilterBar()
}