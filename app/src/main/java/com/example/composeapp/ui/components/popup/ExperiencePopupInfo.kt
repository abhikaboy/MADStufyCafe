package com.example.composeapp.ui.components.popup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeapp.R
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.ui.components.button.CustomButton
import com.example.composeapp.ui.components.Tag
import com.example.composeapp.ui.components.button.WifiRateButton
import com.example.composeapp.ui.theme.CardBackground
import com.example.composeapp.ui.theme.ComposeAppTheme
import com.example.composeapp.ui.theme.TagBackground
import com.example.composeapp.ui.viewmodel.ReviewViewModel


@Composable
fun ExperiencePopupInfo(
    cafe: CafeEntity, 
    onBack: () -> Unit, 
    toShareMoreDetails: () -> Unit, 
    onRatingChanged: ((Float) -> Unit)? = null,
    reviewViewModel: ReviewViewModel? = null
) {
    var selectedOutlet by remember { mutableStateOf("") }
    var selectedWifi by remember { mutableStateOf(-1) }
    var userRating by remember { mutableStateOf(cafe.studyRating.toFloat()) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
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
                text = "How was your working experience at ${cafe.name}" +
                        "?",
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
            StarRatingBar(
                maxStars = 5,
                rating = userRating,
                onRatingChanged = { newRating ->
                    userRating = newRating
                    reviewViewModel?.updateOverallRating(newRating.toDouble())
                    onRatingChanged?.invoke(newRating)
                }
            )
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
                        onClick = { 
                            selectedOutlet = "None"
                            reviewViewModel?.updateOutletAccessibility(0.0)
                        }
                    )
                    Tag(
                        text = "Few",
                        isSelected = selectedOutlet == "Few",
                        onClick = { 
                            selectedOutlet = "Few"
                            reviewViewModel?.updateOutletAccessibility(1.0)
                        }
                    )
                    Tag(
                        text = "Some",
                        isSelected = selectedOutlet == "Some",
                        onClick = { 
                            selectedOutlet = "Some"
                            reviewViewModel?.updateOutletAccessibility(2.0)
                        }
                    )
                    Tag(
                        text = "Plenty",
                        isSelected = selectedOutlet == "Plenty",
                        onClick = { 
                            selectedOutlet = "Plenty"
                            reviewViewModel?.updateOutletAccessibility(3.0)
                        }
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
                        onClick = { 
                            selectedWifi = 0
                            reviewViewModel?.updateWifiQuality(0.0)
                        }
                    )
                    WifiRateButton(
                        rating = "1",
                        text = "Poor",
                        isSelected = selectedWifi == 1,
                        onClick = { 
                            selectedWifi = 1
                            reviewViewModel?.updateWifiQuality(1.0)
                        }
                    )
                    WifiRateButton(
                        rating = "2",
                        text = "Good",
                        isSelected = selectedWifi == 2,
                        onClick = { 
                            selectedWifi = 2
                            reviewViewModel?.updateWifiQuality(2.0)
                        }
                    )
                    WifiRateButton(
                        rating = "3",
                        text = "Great",
                        isSelected = selectedWifi == 3,
                        onClick = { 
                            selectedWifi = 3
                            reviewViewModel?.updateWifiQuality(3.0)
                        }
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
                    onClick = { onBack() }
                )
                CustomButton(
                    text = "Share More Details",
                    onClick = { toShareMoreDetails() }
                )
            }
        }
    }
}

@Composable
fun StarRatingBar(
    maxStars: Int = 5,
    rating: Float,
    onRatingChanged: (Float) -> Unit
) {
    val density = LocalDensity.current.density
    val starSize = (14f * density).dp
    val starSpacing = (2f * density).dp

    Row(
        modifier = Modifier.selectableGroup(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..maxStars) {
            val isSelected = i <= rating
            val icon = if (isSelected) R.drawable.filled_star else R.drawable.star_icon

            Image(
                painter = painterResource(icon),
                contentDescription = "Rate $i stars",
                modifier = Modifier
                    .selectable(
                        selected = isSelected,
                        onClick = {
                            onRatingChanged(i.toFloat())
                        }
                    )
                    .size(starSize)
                    .padding(2.dp)
            )

            if (i < maxStars) {
                Spacer(modifier = Modifier.width(starSpacing))
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewExperiencePopupInfo() {
    val cafe = CafeEntity(
        name = "Bean & Brew",
        address = "123 Main Street, Boston, MA",
        studyRating = 4,
        powerOutlets = "Many",
        wifiQuality = "Excellent",
        atmosphereTags = "Cozy,Rustic,Traditional,Warm,Clean",
        energyLevelTags = "Quiet,Low-Key,Tranquil,Moderate,Average",
        studyFriendlyTags = "Study-Haven,Good,Decent,Mixed,Fair",
        imageUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=400",
        ratingImageUrls = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=400,https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400,https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400"
    )
    ComposeAppTheme {
        ExperiencePopupInfo(cafe, onBack = {}, toShareMoreDetails = {})
    }
}