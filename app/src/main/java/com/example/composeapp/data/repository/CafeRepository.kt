package com.example.composeapp.data.repository

import com.example.composeapp.data.database.CafeDao
import com.example.composeapp.data.database.CafeEntity
import kotlinx.coroutines.flow.Flow

class CafeRepository(private val cafeDao: CafeDao) {
    val allCafes: Flow<List<CafeEntity>> = cafeDao.getAllCafes()

    suspend fun getCafeById(id: Long): CafeEntity? {
        return cafeDao.getCafeById(id)
    }

    suspend fun insertCafe(cafe: CafeEntity): Long {
        return cafeDao.insertCafe(cafe)
    }

    suspend fun insertCafes(cafes: List<CafeEntity>) {
        cafeDao.insertCafes(cafes)
    }

    suspend fun updateCafe(cafe: CafeEntity) {
        cafeDao.updateCafe(cafe)
    }

    suspend fun deleteCafe(cafe: CafeEntity) {
        cafeDao.deleteCafe(cafe)
    }

    suspend fun deleteAllCafes() {
        cafeDao.deleteAllCafes()
    }

    fun searchCafes(query: String): Flow<List<CafeEntity>> {
        return cafeDao.searchCafes(query)
    }
} 