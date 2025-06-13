package com.example.composeapp.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CafeDao {
    @Query("SELECT * FROM cafes")
    fun getAllCafes(): Flow<List<CafeEntity>>

    @Query("SELECT * FROM cafes WHERE id = :id")
    suspend fun getCafeById(id: Long): CafeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCafe(cafe: CafeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCafes(cafes: List<CafeEntity>)

    @Update
    suspend fun updateCafe(cafe: CafeEntity)

    @Delete
    suspend fun deleteCafe(cafe: CafeEntity)

    @Query("DELETE FROM cafes")
    suspend fun deleteAllCafes()

    @Query("SELECT * FROM cafes WHERE name LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchCafes(query: String): Flow<List<CafeEntity>>
} 