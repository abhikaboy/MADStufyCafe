package com.example.composeapp.data.repository.interfaces

import androidx.lifecycle.LiveData
import com.example.composeapp.data.network.ApiResult
import com.example.composeapp.data.network.PhotoCreate
import com.example.composeapp.data.network.Review
import com.example.composeapp.data.network.ReviewCreate
import com.example.composeapp.data.network.ReviewUpdate
import okhttp3.MultipartBody

interface ReviewRepositoryInterface {
    suspend fun createReview(review: ReviewCreate): ApiResult<Review>

    fun getReviewById(reviewId: String): LiveData<ApiResult<Review>>

    suspend fun updateReview(reviewId: String, review: ReviewUpdate): ApiResult<Review>

    suspend fun deleteReview(reviewId: String): ApiResult<Map<String, String>>

    fun getReviewsByStudySpot(studySpotId: String): LiveData<ApiResult<List<Review>>>

    fun getReviewsByUser(userId: String): LiveData<ApiResult<List<Review>>>

    suspend fun addPhotoToReview(reviewId: String, photo: PhotoCreate): ApiResult<Review>

    suspend fun uploadPhotoToReview(
        reviewId: String,
        file: MultipartBody.Part,
        caption: String = ""
    ): ApiResult<Review>
}