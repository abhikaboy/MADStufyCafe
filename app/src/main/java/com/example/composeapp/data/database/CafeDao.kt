package com.example.composeapp.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CafeDao {
    @Query("SELECT * FROM cafes")
    fun getAllCafes(): Flow<List<CafeEntity>>

    @Query("SELECT * FROM cafes")
    suspend fun getAllCafesSync(): List<CafeEntity>

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

    @Query("SELECT * FROM cafes WHERE name LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    suspend fun searchCafesSync(query: String): List<CafeEntity>

    @Query("SELECT * FROM cafes WHERE isBookmarked = 1")
    fun getBookmarkedCafes(): Flow<List<CafeEntity>>

    @Query("UPDATE cafes SET isBookmarked = :isBookmarked WHERE id = :cafeId")
    suspend fun updateBookmarkStatus(cafeId: Long, isBookmarked: Boolean)

    @Query("SELECT * FROM cafes WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLng AND :maxLng")
    suspend fun getCafesInBounds(minLat: Double, maxLat: Double, minLng: Double, maxLng: Double): List<CafeEntity>

    @Query("SELECT * FROM cafes WHERE studyRating >= :minRating")
    suspend fun getCafesByMinRating(minRating: Int): List<CafeEntity>

    @Query("SELECT * FROM cafes WHERE tags LIKE '%' || :amenity || '%'")
    suspend fun getCafesByAmenity(amenity: String): List<CafeEntity>

    @Query("SELECT COUNT(*) FROM cafes")
    suspend fun getCafeCount(): Int
} 