package com.example.composeapp.ui.components.popup

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.composeapp.R
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import com.example.composeapp.ui.theme.CardBackground
import com.example.composeapp.ui.theme.ComposeAppTheme
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.ui.components.button.CustomButton
import com.example.composeapp.ui.theme.TagBackground
import com.example.composeapp.ui.viewmodel.ReviewViewModel

@Composable
fun UploadPhotoInfo(
    cafe: CafeEntity, 
    onBack: () -> Unit,  
    completeReview: () -> Unit,
    reviewViewModel: ReviewViewModel? = null
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Observe review creation and photo upload state
    val reviewCreated = reviewViewModel?.reviewCreated?.observeAsState()?.value
    val isUploadingPhoto = reviewViewModel?.isUploadingPhoto?.observeAsState(false)?.value ?: false
    val photoUploadSuccess = reviewViewModel?.photoUploadSuccess?.observeAsState()?.value
    val errorMessage = reviewViewModel?.errorMessage?.observeAsState()?.value
    
    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            // Upload photo if review has been created
            reviewCreated?.let { review ->
                uploadPhoto(context, reviewViewModel, review.id, it)
            }
        }
    }
    
    // Don't auto-complete here - let CafePopup handle it
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
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Photo",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(8.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.upload_image),
                        contentDescription = "Image Upload",
                        modifier = Modifier.size(150.dp).padding(30.dp)
                    )
                }
            }
            
            // Add photo selection button
            if (selectedImageUri == null && !isUploadingPhoto) {
                Button(
                    onClick = { 
                        photoPickerLauncher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Photo from Gallery")
                }
            }
            
            // Show upload status
            if (isUploadingPhoto == true) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Uploading photo...")
                }
            }
            
            // Show error message if any
            errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
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
                    text = if (selectedImageUri != null && photoUploadSuccess != true) "Skip Photo" else "Complete Review",
                    onClick = { 
                        if (selectedImageUri == null) {
                            // No photo selected, complete without photo
                            completeReview()
                        } else if (photoUploadSuccess == true) {
                            // Photo already uploaded, complete review
                            completeReview()
                        } else {
                            // Photo selected but not uploaded, skip photo upload
                            completeReview()
                        }
                    },
                    enabled = !isUploadingPhoto
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

// Helper function to create multipart body from URI and upload photo
private fun uploadPhoto(
    context: android.content.Context,
    reviewViewModel: ReviewViewModel?,
    reviewId: String,
    imageUri: Uri
) {
    android.util.Log.d("UploadPhotoInfo", "uploadPhoto() called with reviewId: $reviewId, imageUri: $imageUri")
    try {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        inputStream?.let { stream ->
            val bytes = stream.readBytes()
            android.util.Log.d("UploadPhotoInfo", "Image bytes read: ${bytes.size} bytes")
            val requestFile = bytes.toRequestBody("image/*".toMediaType())
            val photoPart = okhttp3.MultipartBody.Part.createFormData(
                "file",
                "photo.jpg",
                requestFile
            )
            
            android.util.Log.d("UploadPhotoInfo", "Calling reviewViewModel.uploadPhotoToReview()")
            reviewViewModel?.uploadPhotoToReview(reviewId, photoPart)
        } ?: run {
            android.util.Log.e("UploadPhotoInfo", "Failed to open input stream for URI: $imageUri")
        }
    } catch (e: Exception) {
        android.util.Log.e("UploadPhotoInfo", "Error uploading photo: ${e.message}")
    }
}