package com.example.composeapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.composeapp.data.database.CafeDao
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.data.network.ApiService
import com.example.composeapp.data.network.ApiResult
import com.example.composeapp.data.network.Cafe
import com.example.composeapp.data.network.safeApiCall
import com.example.composeapp.data.network.toEntity
import com.example.composeapp.utils.LocationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CafeRepository (
    private val cafeDao: CafeDao,
    private val apiService: ApiService,
    private val locationHelper: LocationHelper? = null
) : CafeRepositoryInterface {
    override val allCafes: Flow<List<CafeEntity>> = cafeDao.getAllCafes()

    override fun getAllCafesLiveData(): LiveData<ApiResult<List<CafeEntity>>> = liveData {
        try {
            // First emit cached data
            val cachedCafes = withContext(Dispatchers.IO) {
                cafeDao.getAllCafesSync()
            }
            if (cachedCafes.isNotEmpty()) {
                emit(ApiResult.Success(cachedCafes))
            }

            // Try to get user's location and fetch nearby cafes first
            var apiResult: ApiResult<List<Cafe>>? = null

            try {
                var userLocation = locationHelper?.getCurrentLocation()
                android.util.Log.d("CafeRepository", "LocationHelper result: $userLocation")

                // If no location helper, permission, or last known location, use default location
                if (userLocation == null) {
                    userLocation = LocationHelper.DEFAULT_LOCATION
                    android.util.Log.d("CafeRepository", "Using default location: $userLocation")
                } else {
                    android.util.Log.d("CafeRepository", "Using actual location: $userLocation")
                }

                // Try nearby cafes first
                android.util.Log.d("CafeRepository", "Calling nearby cafes API with location: lat=${userLocation.latitude}, lng=${userLocation.longitude}")
                apiResult = safeApiCall("Get Nearby Cafes") {
                    apiService.findNearbyCafes(
                        longitude = userLocation.longitude,
                        latitude = userLocation.latitude,
                        maxDistance = 10000.0 // 10km radius
                    )
                }

                // If nearby API call fails, we'll fall back to generic call
                if (apiResult is ApiResult.Error) {
                    android.util.Log.w("CafeRepository", "Nearby API call failed, will fallback to generic: ${(apiResult as ApiResult.Error).message}")
                    apiResult = null
                }
            } catch (e: Exception) {
                // Location failed, will fallback to generic call
                android.util.Log.e("CafeRepository", "Location/nearby call exception, will fallback to generic: ${e.message}")
                apiResult = null
            }

            // Fallback to generic get all cafes if location-based fetch failed or no location helper
            if (apiResult == null) {
                apiResult = safeApiCall("Get All Cafes") { apiService.getAllCafes() }
            }

            // Use local variable to avoid smart cast issues
            val finalApiResult = apiResult
            when (finalApiResult) {
                is ApiResult.Success -> {
                    val entities = finalApiResult.data.map { it.toEntity() }
                    withContext(Dispatchers.IO) {
                        cafeDao.deleteAllCafes()
                        cafeDao.insertCafes(entities)
                    }
                    emit(ApiResult.Success(entities))
                }
                is ApiResult.Error -> {
                    if (cachedCafes.isEmpty()) {
                        emit(finalApiResult)
                    }
                    // If we have cached data, we don't emit error, just continue with cached data
                }
                null -> {
                    // This shouldn't happen, but handle it gracefully
                    if (cachedCafes.isEmpty()) {
                        emit(ApiResult.Error("Failed to fetch cafes"))
                    }
                }
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.localizedMessage ?: "Database error"))
        }
    }

    override suspend fun getCafeById(id: Long): CafeEntity? {
        return withContext(Dispatchers.IO) {
            cafeDao.getCafeById(id)
        }
    }

    override fun getCafeByIdLiveData(id: Long): LiveData<ApiResult<CafeEntity?>> = liveData {
        try {
            // First emit cached data
            val cachedCafe = withContext(Dispatchers.IO) {
                cafeDao.getCafeById(id)
            }
            if (cachedCafe != null) {
                emit(ApiResult.Success(cachedCafe))
            }

            // Convert long ID back to string for API call
            val apiResult = safeApiCall("Get Cafe by ID") {
                apiService.getCafeById(id.toString())
            }

            when (apiResult) {
                is ApiResult.Success -> {
                    val entity = apiResult.data.toEntity()
                    withContext(Dispatchers.IO) {
                        cafeDao.insertCafe(entity)
                    }
                    emit(ApiResult.Success(entity))
                }
                is ApiResult.Error -> {
                    if (cachedCafe == null) {
                        emit(apiResult)
                    }
                }
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.localizedMessage ?: "Database error"))
        }
    }

    override suspend fun insertCafe(cafe: CafeEntity): Long {
        return withContext(Dispatchers.IO) {
            cafeDao.insertCafe(cafe)
        }
    }

    override suspend fun insertCafes(cafes: List<CafeEntity>) {
        withContext(Dispatchers.IO) {
            cafeDao.insertCafes(cafes)
        }
    }

    override suspend fun updateCafe(cafe: CafeEntity) {
        withContext(Dispatchers.IO) {
            cafeDao.updateCafe(cafe)
        }
    }

    override suspend fun deleteCafe(cafe: CafeEntity) {
        withContext(Dispatchers.IO) {
            cafeDao.deleteCafe(cafe)
        }
    }

    override suspend fun deleteAllCafes() {
        withContext(Dispatchers.IO) {
            cafeDao.deleteAllCafes()
        }
    }

    override fun searchCafes(query: String): Flow<List<CafeEntity>> {
        return cafeDao.searchCafes(query)
    }

    override fun searchCafesLiveData(query: String): LiveData<ApiResult<List<CafeEntity>>> = liveData {
        try {
            // Search in local database first
            val localResults = withContext(Dispatchers.IO) {
                cafeDao.searchCafesSync(query)
            }
            if (localResults.isNotEmpty()) {
                emit(ApiResult.Success(localResults))
            }

            // Then search via API
            val apiResult = safeApiCall("Search Cafes") { apiService.searchCafes(query) }
            when (apiResult) {
                is ApiResult.Success -> {
                    val entities = apiResult.data.map { it.toEntity() }
                    // Update cache with search results
                    withContext(Dispatchers.IO) {
                        entities.forEach { cafe ->
                            cafeDao.insertCafe(cafe)
                        }
                    }
                    emit(ApiResult.Success(entities))
                }
                is ApiResult.Error -> {
                    if (localResults.isEmpty()) {
                        emit(apiResult)
                    }
                }
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.localizedMessage ?: "Search error"))
        }
    }

    override suspend fun refreshCafes(): ApiResult<List<CafeEntity>> {
        return try {
            // Try to get user's location and fetch nearby cafes first
            var apiResult: ApiResult<List<Cafe>>? = null

            try {
                var userLocation = locationHelper?.getCurrentLocation()
                android.util.Log.d("CafeRepository", "REFRESH - LocationHelper result: $userLocation")

                // If no location helper, permission, or last known location, use default location
                if (userLocation == null) {
                    userLocation = LocationHelper.DEFAULT_LOCATION
                    android.util.Log.d("CafeRepository", "REFRESH - Using default location: $userLocation")
                } else {
                    android.util.Log.d("CafeRepository", "REFRESH - Using actual location: $userLocation")
                }

                // Try nearby cafes first
                android.util.Log.d("CafeRepository", "REFRESH - Calling nearby cafes API with location: lat=${userLocation.latitude}, lng=${userLocation.longitude}")
                apiResult = safeApiCall("Refresh Nearby Cafes") {
                    apiService.findNearbyCafes(
                        longitude = userLocation.longitude,
                        latitude = userLocation.latitude,
                        maxDistance = 10000.0 // 10km radius
                    )
                }

                // If nearby API call fails, we'll fall back to generic call
                if (apiResult is ApiResult.Error) {
                    android.util.Log.w("CafeRepository", "REFRESH - Nearby API call failed, will fallback to generic: ${(apiResult as ApiResult.Error).message}")
                    apiResult = null
                }
            } catch (e: Exception) {
                // Location failed, will fallback to generic call
                android.util.Log.e("CafeRepository", "REFRESH - Location/nearby call exception, will fallback to generic: ${e.message}")
                apiResult = null
            }

            // Fallback to generic get all cafes if location-based fetch failed or no location helper
            if (apiResult == null) {
                apiResult = safeApiCall("Refresh All Cafes") { apiService.getAllCafes() }
            }

            when (apiResult) {
                is ApiResult.Success -> {
                    val entities = apiResult.data.map { it.toEntity() }
                    withContext(Dispatchers.IO) {
                        cafeDao.deleteAllCafes()
                        cafeDao.insertCafes(entities)
                    }
                    ApiResult.Success(entities)
                }
                is ApiResult.Error -> apiResult
                null -> ApiResult.Error("Failed to refresh cafes")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Refresh failed")
        }
    }

    override suspend fun bookmarkCafe(cafeId: Long, isBookmarked: Boolean) {
        withContext(Dispatchers.IO) {
            cafeDao.updateBookmarkStatus(cafeId, isBookmarked)
        }
    }

    override fun getBookmarkedCafes(): LiveData<List<CafeEntity>> = liveData {
        cafeDao.getBookmarkedCafes().collect { cafes ->
            emit(cafes)
        }
    }

    // Additional methods based on API capabilities
    override fun findNearbyCafesLiveData(
        longitude: Double,
        latitude: Double,
        maxDistance: Double
    ): LiveData<ApiResult<List<CafeEntity>>> = liveData {
        val apiResult = safeApiCall("Find Nearby Cafes") {
            apiService.findNearbyCafes(longitude, latitude, maxDistance)
        }
        when (apiResult) {
            is ApiResult.Success -> {
                val entities = apiResult.data.map { it.toEntity() }
                // Cache the results
                withContext(Dispatchers.IO) {
                    entities.forEach { cafe ->
                        cafeDao.insertCafe(cafe)
                    }
                }
                emit(ApiResult.Success(entities))
            }
            is ApiResult.Error -> {
                emit(apiResult)
            }
        }
    }

    override fun findCafesByRatingLiveData(minRating: Double): LiveData<ApiResult<List<CafeEntity>>> = liveData {
        val apiResult = safeApiCall("Find Cafes by Rating") { apiService.findCafesByRating(minRating) }
        when (apiResult) {
            is ApiResult.Success -> {
                val entities = apiResult.data.map { it.toEntity() }
                withContext(Dispatchers.IO) {
                    entities.forEach { cafe ->
                        cafeDao.insertCafe(cafe)
                    }
                }
                emit(ApiResult.Success(entities))
            }
            is ApiResult.Error -> {
                emit(apiResult)
            }
        }
    }

    override fun findCafesByAmenitiesLiveData(amenities: List<String>): LiveData<ApiResult<List<CafeEntity>>> = liveData {
        val apiResult = safeApiCall("Find Cafes by Amenities") { apiService.findCafesByAmenities(amenities) }
        when (apiResult) {
            is ApiResult.Success -> {
                val entities = apiResult.data.map { it.toEntity() }
                withContext(Dispatchers.IO) {
                    entities.forEach { cafe ->
                        cafeDao.insertCafe(cafe)
                    }
                }
                emit(ApiResult.Success(entities))
            }
            is ApiResult.Error -> {
                emit(apiResult)
            }
        }
    }
}