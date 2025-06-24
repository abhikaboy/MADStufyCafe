package com.example.composeapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.composeapp.data.network.*
import com.example.composeapp.data.repository.interfaces.ReviewRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReviewRepository(private val apiService: ApiService) : ReviewRepositoryInterface {
    
    // Create a new review
    override suspend fun createReview(review: ReviewCreate): ApiResult<Review> {
        return withContext(Dispatchers.IO) {
            safeApiCall("Create Review") { 
                apiService.createReview(review)
            }
        }
    }
    
    // Get review by ID
    override fun getReviewById(reviewId: String): LiveData<ApiResult<Review>> = liveData {
        emit(safeApiCall("Get Review by ID") { 
            apiService.getReviewById(reviewId) 
        })
    }
    
    // Update review
    override suspend fun updateReview(reviewId: String, review: ReviewUpdate): ApiResult<Review> {
        return withContext(Dispatchers.IO) {
            safeApiCall("Update Review") { 
                apiService.updateReview(reviewId, review)
            }
        }
    }
    
    // Delete review
    override suspend fun deleteReview(reviewId: String): ApiResult<Map<String, String>> {
        return withContext(Dispatchers.IO) {
            safeApiCall("Delete Review") { 
                apiService.deleteReview(reviewId)
            }
        }
    }
    
    // Get reviews for a specific study spot/cafe
    override fun getReviewsByStudySpot(studySpotId: String): LiveData<ApiResult<List<Review>>> = liveData {
        emit(safeApiCall("Get Reviews by Study Spot") { 
            apiService.getReviewsByStudySpot(studySpotId) 
        })
    }
    
    // Get reviews for a specific user
    override fun getReviewsByUser(userId: String): LiveData<ApiResult<List<Review>>> = liveData {
        emit(safeApiCall("Get Reviews by User") { 
            apiService.getReviewsByUser(userId) 
        })
    }
    
    // Add photo to review
    override suspend fun addPhotoToReview(reviewId: String, photo: PhotoCreate): ApiResult<Review> {
        return withContext(Dispatchers.IO) {
            safeApiCall("Add Photo to Review") { 
                apiService.addPhotoToReview(reviewId, photo)
            }
        }
    }
    
    // Upload photo to review (with file upload)
    override suspend fun uploadPhotoToReview(
        reviewId: String, 
        file: okhttp3.MultipartBody.Part, 
        caption: String
    ): ApiResult<Review> {
        return withContext(Dispatchers.IO) {
            safeApiCall("Upload Photo to Review") { 
                apiService.uploadPhotoToReview(reviewId, file, caption)
            }
        }
    }
} 