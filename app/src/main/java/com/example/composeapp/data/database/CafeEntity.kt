package com.example.composeapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cafes")
data class CafeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val apiId: String = "",
    val name: String,
    val address: String,
    val description: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val phone: String? = null,
    val website: String? = null,
    val hours: String? = null,
    val priceRange: String? = null,
    val tags: String = "",
    val studyRating: Int = 0,
    val ambianceRating: Int = 0,
    val wifiQuality: String = "Unknown",
    val powerOutlets: String = "Unknown",
    val noiseLevel: String = "Medium",
    val seatingCapacity: Int? = null,
    val hasParking: Boolean = false,
    val hasFood: Boolean = true,
    val hasCoffee: Boolean = true,
    val hasStudyArea: Boolean = true,
    val imageUrl: String = "",
    val thumbnailUrl: String = "",
    val atmosphereTags: String = "",
    val energyLevelTags: String = "",
    val studyFriendlyTags: String = "",
    val ratingImageUrls: String = "",
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val isBookmarked: Boolean = false
)