package com.example.composeapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.data.network.ApiResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCafeRepository : CafeRepositoryInterface {
    // Tracking flags
    var refreshCafesWasCalled = false
    var bookmarkCafeWasCalled = false
    var findNearbyCafesWasCalled = false
    var findCafesByRatingWasCalled = false
    var findCafesByAmenitiesWasCalled = false

    // Last parameters
    var lastSearchQuery = ""
    var lastSelectedCafeId = 0L
    var lastBookmarkCafeId = 0L
    var lastBookmarkStatus = false
    var lastLongitude = 0.0
    var lastLatitude = 0.0
    var lastMaxDistance = 0.0
    var lastMinRating = 0.0
    var lastAmenities = emptyList<String>()

    // Test configuration
    var shouldReturnError = false
    var errorMessage = "Test error"

    private val _allCafes = MutableLiveData<ApiResult<List<CafeEntity>>>()
    private val _cafes = mutableListOf<CafeEntity>()

     override val allCafes: Flow<List<CafeEntity>> = MutableStateFlow(_cafes)

     override fun getAllCafesLiveData(): LiveData<ApiResult<List<CafeEntity>>> {
        if (shouldReturnError) {
            _allCafes.value = ApiResult.Error(errorMessage)
        } else {
            _allCafes.value = ApiResult.Success(_cafes)
        }
        return _allCafes
    }

     override suspend fun refreshCafes(): ApiResult<List<CafeEntity>> {
        refreshCafesWasCalled = true
        return if (shouldReturnError) {
            ApiResult.Error(errorMessage)
        } else {
            ApiResult.Success(_cafes)
        }
    }

    override suspend fun bookmarkCafe(cafeId: Long, isBookmarked: Boolean) {
        bookmarkCafeWasCalled = true
        lastBookmarkCafeId = cafeId
        lastBookmarkStatus = isBookmarked
    }

    override fun searchCafesLiveData(query: String): LiveData<ApiResult<List<CafeEntity>>> {
        lastSearchQuery = query
        return MutableLiveData(ApiResult.Success(_cafes.filter {
            it.name.contains(query, ignoreCase = true)
        }))
    }

    override fun getCafeByIdLiveData(id: Long): LiveData<ApiResult<CafeEntity?>> {
        lastSelectedCafeId = id
        val cafe = _cafes.find { it.id == id }
        return MutableLiveData(ApiResult.Success(cafe))
    }

    override fun findNearbyCafesLiveData(
        longitude: Double,
        latitude: Double,
        maxDistance: Double
    ): LiveData<ApiResult<List<CafeEntity>>> {
        findNearbyCafesWasCalled = true
        lastLongitude = longitude
        lastLatitude = latitude
        lastMaxDistance = maxDistance
        return MutableLiveData(ApiResult.Success(_cafes))
    }

    override fun findCafesByRatingLiveData(minRating: Double): LiveData<ApiResult<List<CafeEntity>>> {
        findCafesByRatingWasCalled = true
        lastMinRating = minRating
        return MutableLiveData(ApiResult.Success(_cafes))
    }

    override fun findCafesByAmenitiesLiveData(amenities: List<String>): LiveData<ApiResult<List<CafeEntity>>> {
        findCafesByAmenitiesWasCalled = true
        lastAmenities = amenities
        return MutableLiveData(ApiResult.Success(_cafes))
    }

    fun setCafes(cafes: List<CafeEntity>) {
        _cafes.clear()
        _cafes.addAll(cafes)
        _allCafes.value = ApiResult.Success(_cafes)
    }

    fun resetTrackingFlags() {
        refreshCafesWasCalled = false
        bookmarkCafeWasCalled = false
        findNearbyCafesWasCalled = false
        findCafesByRatingWasCalled = false
        findCafesByAmenitiesWasCalled = false
        lastSearchQuery = ""
        lastSelectedCafeId = 0L
        lastBookmarkCafeId = 0L
        lastBookmarkStatus = false
        lastLongitude = 0.0
        lastLatitude = 0.0
        lastMaxDistance = 0.0
        lastMinRating = 0.0
        lastAmenities = emptyList()
    }

    // Implement other required methods with minimal functionality
     override suspend fun getCafeById(id: Long): CafeEntity? = _cafes.find { it.id == id }
     override suspend fun insertCafe(cafe: CafeEntity): Long = cafe.id
     override suspend fun insertCafes(cafes: List<CafeEntity>) { _cafes.addAll(cafes) }
     override suspend fun updateCafe(cafe: CafeEntity) {}
     override suspend fun deleteCafe(cafe: CafeEntity) {}
     override suspend fun deleteAllCafes() { _cafes.clear() }
     override fun searchCafes(query: String): Flow<List<CafeEntity>> = MutableStateFlow(emptyList())
     override fun getBookmarkedCafes(): LiveData<List<CafeEntity>> = MutableLiveData(emptyList())
}