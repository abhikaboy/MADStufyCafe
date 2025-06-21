package com.example.composeapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeapp.R
import com.example.composeapp.ui.theme.CardBackground
import com.example.composeapp.ui.theme.ComposeAppTheme
import com.example.composeapp.ui.theme.TagBackground


@Composable
fun ExperiencePopupInfo() {
    var selectedOutlet by remember { mutableStateOf("") }
    var selectedWifi by remember { mutableStateOf(-1) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "How was your working experience at this cafe?",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(
                modifier = Modifier.height(31.dp)
            )
            Text(
                text = "Overall Study-ability",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),

            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val studyRating = (0)
                repeat(studyRating) {
                    Image(
                        painter = painterResource(id = com.example.composeapp.R.drawable.filled_star),
                        contentDescription = "Filled Star",
                        modifier = Modifier.size(28.dp)
                    )
                }
                repeat(5 - studyRating) {
                    Image(
                        painter = painterResource(id = R.drawable.star_icon),
                        contentDescription = "Empty Star",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(
                modifier = Modifier.height(31.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 23.dp, end = 16.dp, bottom = 23.dp),
                border = BorderStroke(
                    width = 7.dp,
                    color = TagBackground
                ),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardBackground
                )
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    text = "How would you rate the follow aspects?",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
                    text = "Outlet Accessibility",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Tag(
                        text = "None",
                        isSelected = selectedOutlet == "None",
                        onClick = { selectedOutlet = "None" }
                    )
                    Tag(
                        text = "Few",
                        isSelected = selectedOutlet == "Few",
                        onClick = { selectedOutlet = "Few" }
                    )
                    Tag(
                        text = "Some",
                        isSelected = selectedOutlet == "Some",
                        onClick = { selectedOutlet = "Some" }
                    )
                    Tag(
                        text = "Plenty",
                        isSelected = selectedOutlet == "Plenty",
                        onClick = { selectedOutlet = "Plenty" }
                    )
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top=20.dp, end = 20.dp, bottom = 12.dp),
                    text = "Wifi Access and Quality",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WifiRateButton(
                        rating = "0",
                        text = "None",
                        isSelected = selectedWifi == 0,
                        onClick = { selectedWifi = 0 }
                    )
                    WifiRateButton(
                        rating = "1",
                        text = "Poor",
                        isSelected = selectedWifi == 1,
                        onClick = { selectedWifi = 1 }
                    )
                    WifiRateButton(
                        rating = "2",
                        text = "Good",
                        isSelected = selectedWifi == 2,
                        onClick = { selectedWifi = 2 }
                    )
                    WifiRateButton(
                        rating = "3",
                        text = "Great",
                        isSelected = selectedWifi == 3,
                        onClick = { selectedWifi = 3 }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomButton(
                    text = "Back",
                    onClick = { /* Handle leave Review */ }
                )
                CustomButton(
                    text = "Share More Details",
                    onClick = { /* Handle leave Review */ }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewExperiencePopupInfo() {
    ComposeAppTheme {
        ExperiencePopupInfo()
    }
}