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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class CafeRepository(
    private val cafeDao: CafeDao,
    private val apiService: ApiService
) {
    val allCafes: Flow<List<CafeEntity>> = cafeDao.getAllCafes()

    fun getAllCafesLiveData(): LiveData<ApiResult<List<CafeEntity>>> = liveData {
        try {
            // First emit cached data
            val cachedCafes = withContext(Dispatchers.IO) {
                cafeDao.getAllCafesSync()
            }
            if (cachedCafes.isNotEmpty()) {
                emit(ApiResult.Success(cachedCafes))
            }
            
            // Then try to fetch from API
            val apiResult = safeApiCall { apiService.getAllCafes() }
            when (apiResult) {
                is ApiResult.Success -> {
                    val entities = apiResult.data.map { it.toEntity() }
                    withContext(Dispatchers.IO) {
                        cafeDao.deleteAllCafes()
                        cafeDao.insertCafes(entities)
                    }
                    emit(ApiResult.Success(entities))
                }
                is ApiResult.Error -> {
                    if (cachedCafes.isEmpty()) {
                        emit(apiResult)
                    }
                    // If we have cached data, we don't emit error, just continue with cached data
                }
            }
        } catch (e: Exception) {
            emit(ApiResult.Error(e.localizedMessage ?: "Database error"))
        }
    }

    suspend fun getCafeById(id: Long): CafeEntity? {
        return withContext(Dispatchers.IO) {
            cafeDao.getCafeById(id)
        }
    }

    fun getCafeByIdLiveData(id: Long): LiveData<ApiResult<CafeEntity?>> = liveData {
        try {
            // First emit cached data
            val cachedCafe = withContext(Dispatchers.IO) {
                cafeDao.getCafeById(id)
            }
            if (cachedCafe != null) {
                emit(ApiResult.Success(cachedCafe))
            }
            
            // Convert long ID back to string for API call
            val apiResult = safeApiCall { 
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

    suspend fun insertCafe(cafe: CafeEntity): Long {
        return withContext(Dispatchers.IO) {
            cafeDao.insertCafe(cafe)
        }
    }

    suspend fun insertCafes(cafes: List<CafeEntity>) {
        withContext(Dispatchers.IO) {
            cafeDao.insertCafes(cafes)
        }
    }

    suspend fun updateCafe(cafe: CafeEntity) {
        withContext(Dispatchers.IO) {
            cafeDao.updateCafe(cafe)
        }
    }

    suspend fun deleteCafe(cafe: CafeEntity) {
        withContext(Dispatchers.IO) {
            cafeDao.deleteCafe(cafe)
        }
    }

    suspend fun deleteAllCafes() {
        withContext(Dispatchers.IO) {
            cafeDao.deleteAllCafes()
        }
    }

    fun searchCafes(query: String): Flow<List<CafeEntity>> {
        return cafeDao.searchCafes(query)
    }

    fun searchCafesLiveData(query: String): LiveData<ApiResult<List<CafeEntity>>> = liveData {
        try {
            // Search in local database first
            val localResults = withContext(Dispatchers.IO) {
                cafeDao.searchCafesSync(query)
            }
            if (localResults.isNotEmpty()) {
                emit(ApiResult.Success(localResults))
            }
            
            // Then search via API
            val apiResult = safeApiCall { apiService.searchCafes(query) }
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

    suspend fun refreshCafes(): ApiResult<List<CafeEntity>> {
        return try {
            val apiResult = safeApiCall { apiService.getAllCafes() }
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
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Refresh failed")
        }
    }

    suspend fun bookmarkCafe(cafeId: Long, isBookmarked: Boolean) {
        withContext(Dispatchers.IO) {
            cafeDao.updateBookmarkStatus(cafeId, isBookmarked)
        }
    }

    fun getBookmarkedCafes(): LiveData<List<CafeEntity>> = liveData {
        cafeDao.getBookmarkedCafes().collect { cafes ->
            emit(cafes)
        }
    }

    // Additional methods based on API capabilities
    fun findNearbyCafesLiveData(
        longitude: Double, 
        latitude: Double, 
        maxDistance: Double = 5000.0
    ): LiveData<ApiResult<List<CafeEntity>>> = liveData {
        val apiResult = safeApiCall { 
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

    fun findCafesByRatingLiveData(minRating: Double): LiveData<ApiResult<List<CafeEntity>>> = liveData {
        val apiResult = safeApiCall { apiService.findCafesByRating(minRating) }
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

    fun findCafesByAmenitiesLiveData(amenities: List<String>): LiveData<ApiResult<List<CafeEntity>>> = liveData {
        val apiResult = safeApiCall { apiService.findCafesByAmenities(amenities) }
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