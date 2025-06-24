package com.example.composeapp.data.repository.interfaces

import androidx.lifecycle.LiveData
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.data.network.ApiResult
import kotlinx.coroutines.flow.Flow

interface CafeRepositoryInterface {
    val allCafes: Flow<List<CafeEntity>>

    fun getAllCafesLiveData(): LiveData<ApiResult<List<CafeEntity>>>
    suspend fun getCafeById(id: Long): CafeEntity?
    fun getCafeByIdLiveData(id: Long): LiveData<ApiResult<CafeEntity?>>

    suspend fun insertCafe(cafe: CafeEntity): Long
    suspend fun insertCafes(cafes: List<CafeEntity>)
    suspend fun updateCafe(cafe: CafeEntity)
    suspend fun deleteCafe(cafe: CafeEntity)
    suspend fun deleteAllCafes()

    fun searchCafes(query: String): Flow<List<CafeEntity>>
    fun searchCafesLiveData(query: String): LiveData<ApiResult<List<CafeEntity>>>

    suspend fun refreshCafes(): ApiResult<List<CafeEntity>>

    suspend fun bookmarkCafe(cafeId: Long, isBookmarked: Boolean)
    fun getBookmarkedCafes(): LiveData<List<CafeEntity>>

    fun findNearbyCafesLiveData(
        longitude: Double,
        latitude: Double,
        maxDistance: Double = 5000.0
    ): LiveData<ApiResult<List<CafeEntity>>>

    fun findCafesByRatingLiveData(minRating: Double): LiveData<ApiResult<List<CafeEntity>>>

    fun findCafesByAmenitiesLiveData(amenities: List<String>): LiveData<ApiResult<List<CafeEntity>>>
}