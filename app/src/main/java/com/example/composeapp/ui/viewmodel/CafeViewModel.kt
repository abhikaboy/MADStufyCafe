package com.example.composeapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.data.network.ApiResult
import com.example.composeapp.data.network.CafePhoto
import com.example.composeapp.data.repository.interfaces.CafeRepositoryInterface
import kotlinx.coroutines.launch

class CafeViewModel(private val repository: CafeRepositoryInterface) : ViewModel() {
    
    // LiveData for all cafes
    val allCafes: LiveData<ApiResult<List<CafeEntity>>> = repository.getAllCafesLiveData()
    
    // LiveData for bookmarked cafes
    val bookmarkedCafes: LiveData<List<CafeEntity>> = repository.getBookmarkedCafes()
    
    // LiveData for search results
    private val _searchQuery = MutableLiveData<String>()
    val searchResults: LiveData<ApiResult<List<CafeEntity>>> = _searchQuery.switchMap { query ->
        if (query.isBlank()) {
            repository.getAllCafesLiveData()
        } else {
            repository.searchCafesLiveData(query)
        }
    }
    
    // LiveData for selected cafe details
    private val _selectedCafeId = MutableLiveData<Long>()
    val selectedCafe: LiveData<ApiResult<CafeEntity?>> = _selectedCafeId.switchMap { cafeId ->
        repository.getCafeByIdLiveData(cafeId)
    }
    
    // LiveData for nearby cafes
    private val _nearbyParams = MutableLiveData<NearbyParams>()
    val nearbyCafes: LiveData<ApiResult<List<CafeEntity>>> = _nearbyParams.switchMap { params ->
        repository.findNearbyCafesLiveData(params.longitude, params.latitude, params.maxDistance)
    }
    
    // LiveData for cafes by rating
    private val _minRating = MutableLiveData<Double>()
    val cafesByRating: LiveData<ApiResult<List<CafeEntity>>> = _minRating.switchMap { rating ->
        repository.findCafesByRatingLiveData(rating)
    }
    
    // LiveData for cafes by amenities
    private val _amenities = MutableLiveData<List<String>>()
    val cafesByAmenities: LiveData<ApiResult<List<CafeEntity>>> = _amenities.switchMap { amenities ->
        repository.findCafesByAmenitiesLiveData(amenities)
    }
    
    // Loading and error states
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    // UI state
    private val _isRefreshing = MutableLiveData<Boolean>()
    val isRefreshing: LiveData<Boolean> = _isRefreshing
    
    // LiveData for cafe photos
    private val _cafePhotos = MutableLiveData<ApiResult<List<CafePhoto>>>()
    val cafePhotos: LiveData<ApiResult<List<CafePhoto>>> = _cafePhotos
    
    init {
        // Load cafes on initialization
        refreshCafes()
    }
    
    fun searchCafes(query: String) {
        _searchQuery.value = query
    }
    
    fun selectCafe(cafeId: Long) {
        _selectedCafeId.value = cafeId
    }
    
    fun findNearbyCafes(longitude: Double, latitude: Double, maxDistance: Double = 5000.0) {
        _nearbyParams.value = NearbyParams(longitude, latitude, maxDistance)
    }
    
    fun findCafesByRating(minRating: Double) {
        _minRating.value = minRating
    }
    
    fun findCafesByAmenities(amenities: List<String>) {
        _amenities.value = amenities
    }
    
    fun getCafePhotos(cafeId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getCafePhotos(cafeId)
                _cafePhotos.value = result
                if (result is ApiResult.Error) {
                    _errorMessage.value = result.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load photos: ${e.localizedMessage}"
                _cafePhotos.value = ApiResult.Error("Failed to load photos")
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun refreshCafes() {
        viewModelScope.launch {
            _isRefreshing.value = true
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = repository.refreshCafes()
                when (result) {
                    is ApiResult.Success -> {
                        _errorMessage.value = null
                    }
                    is ApiResult.Error -> {
                        _errorMessage.value = result.message
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
                _isRefreshing.value = false
            }
        }
    }
    
    fun bookmarkCafe(cafeId: Long, isBookmarked: Boolean) {
        viewModelScope.launch {
            try {
                repository.bookmarkCafe(cafeId, isBookmarked)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update bookmark: ${e.localizedMessage}"
            }
        }
    }
    
    fun insertCafe(cafe: CafeEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.insertCafe(cafe)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add cafe: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateCafe(cafe: CafeEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateCafe(cafe)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to update cafe: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteCafe(cafe: CafeEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.deleteCafe(cafe)
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete cafe: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
    }
    
    // Helper function to check if cafes are loaded
    fun isCafesLoaded(): Boolean {
        return allCafes.value is ApiResult.Success
    }
    
    // Helper function to get cafe count
    fun getCafeCount(): Int {
        return when (val result = allCafes.value) {
            is ApiResult.Success -> result.data.size
            else -> 0
        }
    }
}

// Data class for nearby search parameters
data class NearbyParams(
    val longitude: Double,
    val latitude: Double,
    val maxDistance: Double
) 