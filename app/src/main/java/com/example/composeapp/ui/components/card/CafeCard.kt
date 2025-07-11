package com.example.composeapp.ui.components.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.composeapp.R
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.ui.components.tag.LightLabel
import com.example.composeapp.ui.theme.Background
import com.example.composeapp.ui.theme.CardBackground
import com.example.composeapp.ui.theme.ComposeAppTheme
import com.example.composeapp.ui.theme.LargeCardBackground
import com.example.composeapp.ui.theme.Primary


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CafeCard(
    cafe: CafeEntity, 
    onClick: () -> Unit,
    onBookmarkClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = LargeCardBackground
        ),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box {
                CafeImage(cafe.imageUrl)
                IconButton(
                    onClick = onBookmarkClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (cafe.isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (cafe.isBookmarked) "Remove bookmark" else "Add bookmark",
                        tint = if (cafe.isBookmarked) Primary else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Text(
                text = cafe.name,
                style = MaterialTheme.typography.titleLarge,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.location_icon),
                    contentDescription = "Icon",
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp)) // spacing between image and text
                Text(
                    text = cafe.address,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Card 1
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Background
                    ),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp),
                    ) {
                        Text(
                            text = "Study Rating",
                            style = MaterialTheme.typography.bodySmall
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            val rating = cafe.studyRating.coerceIn(0, 5)
                            repeat(rating) {
                                Image(
                                    painter = painterResource(id = R.drawable.filled_star),
                                    contentDescription = "Star",
                                    modifier = Modifier.size(13.dp)
                                )
                            }

                            repeat(5 - rating) {
                                Image(
                                    painter = painterResource(id = R.drawable.star_icon),
                                    contentDescription = "Empty Star",
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }

                // Card 2
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground
                    ),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp),
                    ) {
                        Text(
                            text = "Outlets",
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.bodySmall

                        )
                        Spacer(modifier = Modifier.height(5.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.outlet_icon),
                                contentDescription = "Outlet",
                                modifier = Modifier.size(20.dp)
                            )
                            LightLabel(
                                text = cafe.powerOutlets
                            )
                        }
                    }
                }

                // Card 3
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground
                    ),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                    ) {
                        Text(
                            text = "Wifi",
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .align(Alignment.Start),
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.wifi_icon),
                                contentDescription = "Wifi",
                                modifier = Modifier.size(18.dp)
                            )
                            LightLabel(
                                text = cafe.wifiQuality
                            )

                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CafeImage(imageUrl: String) {
    val isPreview = LocalInspectionMode.current
    if (isPreview) {
        // Show local placeholder drawable in preview
        Image(
            painter = painterResource(id = R.drawable.cafe),
            contentDescription = "Cafe Preview",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(9.dp)),
            contentScale = ContentScale.Crop
        )
    } else {
        // Actual image when running on device
        GlideImage(
            model = imageUrl,
            contentDescription = "Cafe Photo",
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(9.dp)),
            contentScale = ContentScale.Crop,
        )
    }
}

@Preview
@Composable
fun PreviewCafeCard() {
    val cafe = CafeEntity(
        name = "Bean & Brew Cafe",
        address = "123 Coffee Street, Downtown",
        description = "A cozy study cafe",
        tags = "study,wifi,coffee",
        studyRating = 4,
        powerOutlets = "Many",
        wifiQuality = "Excellent",
        imageUrl = "",
        atmosphereTags = "Cozy,Modern",
        energyLevelTags = "Medium",
        studyFriendlyTags = "Very Good"
    )
    ComposeAppTheme {
        CafeCard(cafe = cafe, onClick = {})
    }
}
