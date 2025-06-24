package com.example.composeapp.data.database.fakes

import com.example.composeapp.data.database.CafeDao
import com.example.composeapp.data.database.CafeEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FakeCafeDao : CafeDao {
    private val cafes = mutableListOf<CafeEntity>()
    private val cafesFlow = MutableStateFlow<List<CafeEntity>>(emptyList())

    override suspend fun insertCafe(cafe: CafeEntity): Long {
        cafes.removeAll { it.id == cafe.id }
        cafes.add(cafe)
        cafesFlow.value = cafes.toList()
        return cafe.id
    }

    override suspend fun insertCafes(cafes: List<CafeEntity>) {
        this.cafes.removeAll { cafe -> cafes.any { it.id == cafe.id } }
        this.cafes.addAll(cafes)
        cafesFlow.value = this.cafes.toList()
    }

    override suspend fun getCafeById(id: Long): CafeEntity? {
        return cafes.find { it.id == id }
    }

    override fun getAllCafes(): Flow<List<CafeEntity>> {
        return cafesFlow
    }

    override suspend fun getAllCafesSync(): List<CafeEntity> {
        return cafes.toList()
    }

    override suspend fun deleteAllCafes() {
        cafes.clear()
        cafesFlow.value = emptyList()
    }

    override suspend fun updateCafe(cafe: CafeEntity) {
        cafes.removeAll { it.id == cafe.id }
        cafes.add(cafe)
        cafesFlow.value = cafes.toList()
    }

    override suspend fun deleteCafe(cafe: CafeEntity) {
        cafes.removeAll { it.id == cafe.id }
        cafesFlow.value = cafes.toList()
    }

    override fun searchCafes(query: String): Flow<List<CafeEntity>> {
        return flow {
            val results = cafes.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.address.contains(query, ignoreCase = true)
            }
            emit(results)
        }
    }

    override suspend fun searchCafesSync(query: String): List<CafeEntity> {
        return cafes.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.address.contains(query, ignoreCase = true)
        }
    }

    override fun getBookmarkedCafes(): Flow<List<CafeEntity>> {
        return cafesFlow.map { list ->
            list.filter { it.isBookmarked }
        }
    }

    override suspend fun updateBookmarkStatus(cafeId: Long, isBookmarked: Boolean) {
        val index = cafes.indexOfFirst { it.id == cafeId }
        if (index != -1) {
            cafes[index] = cafes[index].copy(isBookmarked = isBookmarked)
            cafesFlow.value = cafes.toList()
        }
    }

    override suspend fun getCafesInBounds(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): List<CafeEntity> {
        return cafes.filter { cafe ->
            cafe.latitude != null && cafe.longitude != null &&
                    cafe.latitude >= minLat && cafe.latitude <= maxLat &&
                    cafe.longitude >= minLng && cafe.longitude <= maxLng
        }
    }

    override suspend fun getCafesByMinRating(minRating: Int): List<CafeEntity> {
        return cafes.filter { it.studyRating >= minRating }
    }

    override suspend fun getCafesByAmenity(amenity: String): List<CafeEntity> {
        return cafes.filter { cafe ->
            cafe.tags.contains(amenity, ignoreCase = true)
        }
    }

    override suspend fun getCafeCount(): Int {
        return cafes.size
    }
}