package com.example.composeapp.ui.components.popup

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.ui.components.button.CustomButton
import com.example.composeapp.ui.components.tag.Tag
import com.example.composeapp.ui.theme.CardBackground
import com.example.composeapp.ui.theme.ComposeAppTheme
import com.example.composeapp.ui.theme.TagBackground
import com.example.composeapp.ui.viewmodel.ReviewViewModel


@Composable
fun AmbiancePopupInfo(
    cafe: CafeEntity, 
    onBack: () -> Unit, 
    toUploadPhoto: () -> Unit,
    reviewViewModel: ReviewViewModel? = null
) {
    var selectedAtmosphere by remember { mutableStateOf(setOf<String>()) }
    var selectedEnergy by remember { mutableStateOf(setOf<String>()) }
    var selectedStudyFriendly by remember { mutableStateOf(setOf<String>()) }

    val atmosphereTags = listOf(
        "Cozy", "Warm", "Rustic", "Traditional",
        "Modern", "Clean", "Minimalist", "Basic",
        "Industrial", "Plain", "Cold", "Sterile"
    )

    val energyLevelTags = listOf(
        "Tranquil", "Calm", "Quiet", "Low-key",
        "Moderate", "Average", "Lively", "Active",
        "Buzzing", "Loud", "Noisy", "Chaotic"
    )

    val studyFriendlyTags = listOf(
        "Laser Focus", "Study Haven", "Very Quiet",
        "Good", "Decent", "Okay", "Mixed", "Fair",
        "Social", "Poor", "Bad", "Distracting"
    )

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
                text = "Can you tell us more about the ambiance of ${cafe.name}?",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(
                modifier = Modifier.height(31.dp)
            )
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
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
                    text = "Tag which descriptions you find match each of the following aspects.",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 10.dp, bottom = 12.dp),
                    text = "Atmosphere",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Left,
                )
                SelectableTagSection(
                    tags = atmosphereTags,
                    selectedTags = selectedAtmosphere,
                    onTagToggle = { tag ->
                        selectedAtmosphere = if (selectedAtmosphere.contains(tag)) {
                            selectedAtmosphere - tag
                        } else {
                            selectedAtmosphere + tag
                        }
                        reviewViewModel?.updateAtmosphere(selectedAtmosphere.joinToString(","))
                    }
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 20.dp, bottom = 12.dp),
                    text = "Energy Level",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Left,
                )

                SelectableTagSection(
                    tags = energyLevelTags,
                    selectedTags = selectedEnergy,
                    onTagToggle = { tag ->
                        selectedEnergy = if (selectedEnergy.contains(tag)) {
                            selectedEnergy - tag
                        } else {
                            selectedEnergy + tag
                        }
                        reviewViewModel?.updateEnergyLevel(selectedEnergy.joinToString(","))
                    }
                )
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, top = 20.dp, bottom = 12.dp),
                    text = "Study Friendly",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Left,
                )
                SelectableTagSection(
                    tags = studyFriendlyTags,
                    selectedTags = selectedStudyFriendly,
                    onTagToggle = { tag ->
                        selectedStudyFriendly = if (selectedStudyFriendly.contains(tag)) {
                            selectedStudyFriendly - tag
                        } else {
                            selectedStudyFriendly + tag
                        }
                        reviewViewModel?.updateStudyFriendly(selectedStudyFriendly.joinToString(","))
                    }
                )

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
                    text = "Upload Photos",
                    onClick = { toUploadPhoto() }
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SelectableTagSection(
    tags: List<String>,
    selectedTags: Set<String>,
    onTagToggle: (String) -> Unit
) {
    FlowRow(
        modifier = Modifier.padding(start = (20.dp), end = (20.dp))
    ) {
        tags.forEach { tag ->
            Tag(
                text = tag,
                isSelected = selectedTags.contains(tag),
                onClick = { onTagToggle(tag) },
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAmbiancePopupInfo() {
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
        AmbiancePopupInfo(cafe, onBack = {}, toUploadPhoto = {})
    }
}