package com.example.composeapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeapp.R
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.ui.theme.CardBackground
import com.example.composeapp.ui.theme.LargeCardBackground
import com.example.composeapp.ui.theme.TextBackground


@Composable
fun CafeCard(cafe: CafeEntity) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = LargeCardBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painterResource(id = R.drawable.cafe), "Card Image", Modifier
                    .fillMaxWidth()
                    .height(180.dp), contentScale = ContentScale.Crop
            )

            Text(
                text = cafe.name,
                style = MaterialTheme.typography.titleLarge,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically, // aligns image & text nicely
            ) {
                Image(
                    painter = painterResource(id = R.drawable.location_icon),
                    contentDescription = "Icon",
                )
                Spacer(modifier = Modifier.width(8.dp)) // spacing between image and text
                Text(cafe.address)
            }

            //Three card component
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Card 1
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(70.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp),
                    ) {
                        // Top Text
                        Text(
                            text = "Study Rating",
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        //Add vertical spacing
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            val rating = cafe.studyRating.coerceIn(0, 5)
                            repeat(rating) {
                                Image(
                                    painter = painterResource(id = R.drawable.filled_star),
                                    contentDescription = "Star",
                                    modifier = Modifier.size(13.dp) // Adjust star size here
                                )
                            }

                            // Empty stars for the rest
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
                        .height(70.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp),
                    ) {
                        Text(
                            text = "Outlets",
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.outlet_icon),
                                contentDescription = "Outlet",
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = cafe.outletInfo,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .background(
                                        color = TextBackground,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }

                // Card 3
                Card(
                    modifier = Modifier
                        .wrapContentWidth()
                        .height(70.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardBackground
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                    ) {
                        Text(
                            text = "Wifi",
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .align(Alignment.Start),
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.wifi_icon),
                                contentDescription = "Wifi",
                            )
                            Text(
                                text = cafe.wifiQuality,
                                fontSize = 16.sp,
                                modifier = Modifier
                                    .background(
                                        color = TextBackground,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PreviewCafeCard() {
    val cafe = CafeEntity(
        name = "Cafe name",
        address = "Location, location, location",
        tags = "",
        studyRating = 3,
        outletInfo = "Many",
        wifiQuality = "excellent",
        imageUrl = ""
    )
    CafeCard(cafe)
}
