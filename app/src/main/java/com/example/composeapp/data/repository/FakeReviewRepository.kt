package com.example.composeapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.composeapp.data.network.*

class FakeReviewRepository : ReviewRepositoryInterface {
    // Tracking flags
    var createReviewWasCalled = false
    var updateReviewWasCalled = false
    var deleteReviewWasCalled = false
    var uploadPhotoWasCalled = false

    // Private backing properties to avoid conflicts
    private var _createReviewResult: ApiResult<Review>? = null
    private var _updateReviewResult: ApiResult<Review>? = null
    private var _uploadPhotoResult: ApiResult<Review>? = null

    override suspend fun createReview(review: ReviewCreate): ApiResult<Review> {
        createReviewWasCalled = true
        return _createReviewResult ?: ApiResult.Error("Not configured")
    }

    override fun getReviewById(reviewId: String): LiveData<ApiResult<Review>> {
        return MutableLiveData(ApiResult.Error("Not found"))
    }

    override suspend fun updateReview(reviewId: String, review: ReviewUpdate): ApiResult<Review> {
        updateReviewWasCalled = true
        return _updateReviewResult ?: ApiResult.Error("Not configured")
    }

    override suspend fun deleteReview(reviewId: String): ApiResult<Map<String, String>> {
        deleteReviewWasCalled = true
        return ApiResult.Success(mapOf("status" to "success"))
    }

    override fun getReviewsByStudySpot(studySpotId: String): LiveData<ApiResult<List<Review>>> {
        return MutableLiveData(ApiResult.Success(emptyList()))
    }

    override fun getReviewsByUser(userId: String): LiveData<ApiResult<List<Review>>> {
        return MutableLiveData(ApiResult.Success(emptyList()))
    }

    override suspend fun addPhotoToReview(reviewId: String, photo: PhotoCreate): ApiResult<Review> {
        return ApiResult.Error("Not configured")
    }

    override suspend fun uploadPhotoToReview(
        reviewId: String,
        file: okhttp3.MultipartBody.Part,
        caption: String
    ): ApiResult<Review> {
        uploadPhotoWasCalled = true
        return _uploadPhotoResult ?: ApiResult.Error("Not configured")
    }

    // Fixed: Changed method names from set... to configure... to avoid platform declaration clash
    fun configureCreateReviewResult(result: ApiResult<Review>?) {
        _createReviewResult = result
    }

    fun configureUpdateReviewResult(result: ApiResult<Review>?) {
        _updateReviewResult = result
    }

    fun configureUploadPhotoResult(result: ApiResult<Review>?) {
        _uploadPhotoResult = result
    }

    fun resetTrackingFlags() {
        createReviewWasCalled = false
        updateReviewWasCalled = false
        deleteReviewWasCalled = false
        uploadPhotoWasCalled = false
    }
}