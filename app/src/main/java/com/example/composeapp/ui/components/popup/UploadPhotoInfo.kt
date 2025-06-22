package com.example.composeapp.ui.components.popup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeapp.R
import com.example.composeapp.ui.theme.CardBackground
import com.example.composeapp.ui.theme.ComposeAppTheme
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.ui.components.button.CustomButton
import com.example.composeapp.ui.theme.TagBackground

@Composable
fun UploadPhotoInfo(cafe: CafeEntity, onBack: () -> Unit,  completeReview:() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        //.padding(start = 23.dp, end = 16.dp, bottom = 23.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Text(
                text = "Upload a photo of ${cafe.name}",
                style = MaterialTheme.typography.headlineLarge
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
            ){
                Image(
                    painter = painterResource(id = R.drawable.upload_image),
                    contentDescription = "Image Upload",
                    modifier = Modifier.size(150.dp).padding(30.dp)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomButton(
                    text = "Back",
                    onClick = { onBack() }
                )
                CustomButton(
                    text = "Complete Review",
                    onClick = {  }
                )
            }

        }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewUploadPopup() {
    val cafe = CafeEntity(
        id = 1,
        name = "Caffe Bene",
        address = "14 Massachusetts Ave, Boston, MA",
        studyRating = 3,
        powerOutlets = "Some",
        wifiQuality = "Excellent",
        atmosphereTags = "Cozy,Rustic,Traditional,Warm,Clean",
        energyLevelTags = "Quiet,Low-Key,Tranquil,Moderate,Average",
        studyFriendlyTags = "Study-Haven,Good,Decent,Mixed,Fair",
        imageUrl = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=400",
        ratingImageUrls = "https://images.unsplash.com/photo-1501339847302-ac426a4a7cbb?w=400,https://images.unsplash.com/photo-1554118811-1e0d58224f24?w=400,https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400"
    )

    ComposeAppTheme {
        UploadPhotoInfo(cafe, onBack = {}, completeReview = {})
    }
}