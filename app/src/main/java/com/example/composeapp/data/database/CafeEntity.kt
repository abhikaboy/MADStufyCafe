package com.example.composeapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cafes")
data class CafeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val address: String,
    val tags: String = "",
    val studyRating: Int = 0,
    val outletInfo: String = "Unknown",
    val wifiQuality: String = "Unknown",
    val imageUrl: String = "",
    val atmosphereTags: String = "",
    val energyLevelTags: String = "",
    val studyFriendlyTags: String = "",
    val ratingImageUrls: String = "",
)