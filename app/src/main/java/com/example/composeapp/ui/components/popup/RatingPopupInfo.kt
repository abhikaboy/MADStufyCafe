package com.example.composeapp.ui.components.popup

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composeapp.R
import com.example.composeapp.ui.theme.TextPrimary
import com.example.composeapp.ui.theme.TextSecondary
import com.example.composeapp.ui.theme.CardBackground
import com.example.composeapp.ui.theme.ComposeAppTheme
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.ui.components.button.CustomButton
import com.example.composeapp.ui.components.tag.LightLabel
import com.example.composeapp.ui.components.tag.Tag
import com.example.composeapp.ui.viewmodel.CafeViewModel
import com.example.composeapp.ui.viewmodel.ReviewViewModel
import com.example.composeapp.ui.viewmodel.UserViewModel
import com.example.composeapp.data.network.ApiResult
import com.example.composeapp.data.network.CafePhoto

@Composable
fun RatingPopupInfo(
    cafe: CafeEntity, 
    onNext: () -> Unit,
    reviewViewModel: ReviewViewModel? = null,
    onBookmarkClick: ((CafeEntity) -> Unit)? = null,
    userViewModel: UserViewModel? = null,
    currentUserId: String? = null,
    cafeViewModel: CafeViewModel? = null
) {
    val context = LocalContext.current
    
    // Observe cafe photos from the ViewModel
    val cafePhotos = cafeViewModel?.cafePhotos?.observeAsState()?.value
    
    // Fetch photos when the component loads
    LaunchedEffect(cafe.apiId) {
        if (cafeViewModel != null && !cafe.apiId.isNullOrEmpty()) {
            cafeViewModel.getCafePhotos(cafe.apiId)
        }
    }
    
    // Observe bookmark action success to show toast
    userViewModel?.bookmarkActionSuccess?.observeAsState()?.value?.let { message ->
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            userViewModel.clearBookmarkSuccess()
        }
    }
    
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomButton(
                    text = if (cafe.isBookmarked) "Bookmarked" else "Bookmark",
                    onClick = { 
                        if (currentUserId != null && cafe.apiId.isNotEmpty()) {
                            if (cafe.isBookmarked) {
                                userViewModel?.deleteBookmark(currentUserId, cafe.apiId)
                            } else {
                                userViewModel?.createBookmark(currentUserId, cafe.apiId)
                            }
                        }
                        onBookmarkClick?.invoke(cafe)
                    }
                )
                CustomButton(
                    text = "Leave a Review",
                    onClick = { onNext() }
                )
            }

            Text(
                text = cafe.name,
                style = MaterialTheme.typography.headlineLarge
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.location_icon),
                    contentDescription = "Location Icon",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = cafe.address,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(
                    width = 0.5.dp,
                    color = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Study Rating",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            val studyRating = cafe.studyRating.coerceIn(0, 5)
                            repeat(studyRating) {
                                Image(
                                    painter = painterResource(id = R.drawable.filled_star),
                                    contentDescription = "Filled Star",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            repeat(5 - studyRating) {
                                Image(
                                    painter = painterResource(id = R.drawable.star_icon),
                                    contentDescription = "Empty Star",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Outlets",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.outlet_icon),
                                contentDescription = "Outlet Icon",
                                modifier = Modifier.size(20.dp)
                            )
                            LightLabel(text = cafe.powerOutlets)
                        }
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Wifi",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.wifi_icon),
                                contentDescription = "Wifi Icon",
                                modifier = Modifier.size(16.dp)
                            )
                            LightLabel(text = cafe.wifiQuality)
                        }
                    }
                }
            }

            TagSection(
                title = "Atmosphere",
                tags = cafe.atmosphereTags.stringToList()
            )

            TagSection(
                title = "Energy Level",
                tags = cafe.energyLevelTags.stringToList()
            )

            TagSection(
                title = "Study-Friendly",
                tags = cafe.studyFriendlyTags.stringToList()
            )

            PhotosSection(
                title = "Recent Photos",
                cafePhotos = cafePhotos
            )
        }
    }
}

@Composable
fun TagSection(
    title: String,
    tags: List<String>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        if (tags.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tags) { tag ->
                    Tag(text = tag)
                }
            }
        } else {
            Text(
                text = "No tags available",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun PhotosSection(
    title: String,
    cafePhotos: ApiResult<List<CafePhoto>>?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        when (cafePhotos) {
            is ApiResult.Success -> {
                val photos = cafePhotos.data
                if (photos.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(120.dp)
                    ) {
                        val photosToShow = photos.take(4)

                        photosToShow.forEachIndexed { index, photo ->
                            val isLastItem = index == 3 && photos.size > 4
                            val remainingCount = if (photos.size > 4) photos.size - 4 else 0

                            PhotoCard(
                                imageUrl = photo.url,
                                caption = photo.caption,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                isLastItem = isLastItem,
                                remainingCount = remainingCount
                            )
                        }
                        repeat(4 - photosToShow.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No photos available",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }
            is ApiResult.Error -> {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Failed to load photos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
            null -> {
                // Loading state
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PhotoCard(
    imageUrl: String,
    caption: String? = null,
    modifier: Modifier = Modifier,
    isLastItem: Boolean = false,
    remainingCount: Int = 0
) {
    Card(
        modifier = modifier.aspectRatio(1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box {
            GlideImage(
                model = imageUrl,
                contentDescription = "Cafe Photo",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            if (isLastItem && remainingCount > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Color.Black.copy(alpha = 0.6f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+$remainingCount More",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

fun String.stringToList(): List<String> {
    return if (this.isEmpty()) {
        emptyList()
    } else {
        this.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    }
}

@Preview(showBackground = false)
@Composable
fun PreviewRatingPopupInfo() {
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
        ratingImageUrls = "",
        apiId = "sample-cafe-id"
    )

    ComposeAppTheme {
        RatingPopupInfo(cafe, onNext = {})
    }
}