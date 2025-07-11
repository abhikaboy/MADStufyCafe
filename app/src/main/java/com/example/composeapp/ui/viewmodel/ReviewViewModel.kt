package com.example.composeapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.data.network.*
import com.example.composeapp.data.repository.interfaces.ReviewRepositoryInterface
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ReviewViewModel(private val reviewRepository: ReviewRepositoryInterface) : ViewModel() {
    
    // Current review being created
    private val _currentReview = MutableLiveData<ReviewCreate?>()
    val currentReview: LiveData<ReviewCreate?> = _currentReview
    
    // Review creation state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    private val _reviewCreated = MutableLiveData<Review?>()
    val reviewCreated: LiveData<Review?> = _reviewCreated
    
    // Review form data
    private val _overallRating = MutableLiveData<Double>(0.0)
    val overallRating: LiveData<Double> = _overallRating
    
    private val _outletAccessibility = MutableLiveData<Double>(0.0)
    val outletAccessibility: LiveData<Double> = _outletAccessibility
    
    private val _wifiQuality = MutableLiveData<Double>(0.0)
    val wifiQuality: LiveData<Double> = _wifiQuality
    
    private val _atmosphere = MutableLiveData<String>("")
    val atmosphere: LiveData<String> = _atmosphere
    
    private val _energyLevel = MutableLiveData<String>("")
    val energyLevel: LiveData<String> = _energyLevel
    
    private val _studyFriendly = MutableLiveData<String>("")
    val studyFriendly: LiveData<String> = _studyFriendly
    
    // Photo upload state
    private val _uploadedPhotos = MutableLiveData<List<String>>(emptyList())
    val uploadedPhotos: LiveData<List<String>> = _uploadedPhotos
    
    private val _isUploadingPhoto = MutableLiveData(false)
    val isUploadingPhoto: LiveData<Boolean> = _isUploadingPhoto
    
    private val _photoUploadSuccess = MutableLiveData<Boolean?>(null)
    val photoUploadSuccess: LiveData<Boolean?> = _photoUploadSuccess
    
    fun startReview(cafeId: String, userId: String) {
        _currentReview.value = ReviewCreate(
            study_spot_id = cafeId,
            user_id = userId,
            overall_rating = 0.0,
            outlet_accessibility = 0.0,
            wifi_quality = 0.0
        )
        clearForm()
    }
    
    fun updateOverallRating(rating: Double) {
        _overallRating.value = rating
        updateCurrentReview()
    }
    
    fun updateOutletAccessibility(rating: Double) {
        _outletAccessibility.value = rating
        updateCurrentReview()
    }
    
    fun updateWifiQuality(rating: Double) {
        _wifiQuality.value = rating
        updateCurrentReview()
    }
    
    fun updateAtmosphere(atmosphere: String) {
        _atmosphere.value = atmosphere
        updateCurrentReview()
    }
    
    fun updateEnergyLevel(energyLevel: String) {
        _energyLevel.value = energyLevel
        updateCurrentReview()
    }
    
    fun updateStudyFriendly(studyFriendly: String) {
        _studyFriendly.value = studyFriendly
        updateCurrentReview()
    }
    
    private fun updateCurrentReview() {
        _currentReview.value?.let { review ->
            _currentReview.value = review.copy(
                overall_rating = _overallRating.value ?: 0.0,
                outlet_accessibility = _outletAccessibility.value ?: 0.0,
                wifi_quality = _wifiQuality.value ?: 0.0,
                atmosphere = _atmosphere.value?.takeIf { it.isNotBlank() }?.let { listOf(it) },
                energy_level = _energyLevel.value?.takeIf { it.isNotBlank() }?.let { listOf(it) },
                study_friendly = _studyFriendly.value?.takeIf { it.isNotBlank() }?.let { listOf(it) }
            )
        }
    }
    
    fun submitReview() {
        val review = _currentReview.value

        if (review == null) {
            _errorMessage.value = "No review data to submit"
            return
        }
        
        if (review.overall_rating <= 0) {
            _errorMessage.value = "Please provide an overall rating"
            return
        }
        
        _isLoading.value = true
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val result = reviewRepository.createReview(review)
                when (result) {
                    is ApiResult.Success -> {
                        _reviewCreated.value = result.data
                        _errorMessage.value = null
                    }
                    is ApiResult.Error -> {
                        _errorMessage.value = result.message
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to submit review"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addPhoto(photoUrl: String) {
        val currentPhotos = _uploadedPhotos.value ?: emptyList()
        _uploadedPhotos.value = currentPhotos + photoUrl
    }
    
    fun removePhoto(photoUrl: String) {
        val currentPhotos = _uploadedPhotos.value ?: emptyList()
        _uploadedPhotos.value = currentPhotos.filter { it != photoUrl }
    }
    
    fun clearForm() {
        _overallRating.value = 0.0
        _outletAccessibility.value = 0.0
        _wifiQuality.value = 0.0
        _atmosphere.value = ""
        _energyLevel.value = ""
        _studyFriendly.value = ""
        _uploadedPhotos.value = emptyList()
        _errorMessage.value = null
    }
    
    fun resetAllStates() {
        clearForm()
        _reviewCreated.value = null
        _photoUploadSuccess.value = null
        _isUploadingPhoto.value = false
        _currentReview.value = null
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun uploadPhotoToReview(reviewId: String, photoPart: MultipartBody.Part, caption: String = "") {
        _isUploadingPhoto.value = true
        _photoUploadSuccess.value = null
        _errorMessage.value = null
        
        viewModelScope.launch {
            try {
                val result = reviewRepository.uploadPhotoToReview(reviewId, photoPart, caption)
                when (result) {
                    is ApiResult.Success -> {
                        _photoUploadSuccess.value = true
                        // Update the review data with the new photo
                        _reviewCreated.value = result.data
                    }
                    is ApiResult.Error -> {
                        _photoUploadSuccess.value = false
                        _errorMessage.value = result.message
                    }
                }
            } catch (e: Exception) {
                _photoUploadSuccess.value = false
                _errorMessage.value = e.localizedMessage ?: "Failed to upload photo"
            } finally {
                _isUploadingPhoto.value = false
            }
        }
    }
    
    fun clearPhotoUploadState() {
        _photoUploadSuccess.value = null
        _isUploadingPhoto.value = false
    }
    
    fun isReviewValid(): Boolean {
        val review = _currentReview.value
        return review != null && review.overall_rating > 0
    }
    
    fun getReviewProgress(): Float {
        var progress = 0f
        val totalSteps = 6f // overall, outlet, wifi, atmosphere, energy, study-friendly
        
        if ((_overallRating.value ?: 0.0) > 0) progress += 1f
        if ((_outletAccessibility.value ?: 0.0) > 0) progress += 1f
        if ((_wifiQuality.value ?: 0.0) > 0) progress += 1f
        if (!_atmosphere.value.isNullOrBlank()) progress += 1f
        if (!_energyLevel.value.isNullOrBlank()) progress += 1f
        if (!_studyFriendly.value.isNullOrBlank()) progress += 1f
        
        return progress / totalSteps
    }
} 