package com.example.composeapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cafes")
data class RatingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val address: String,
    val studyRating: Int,
    val outletInfo: String,
    val wifiQuality: String,
    val atmosphereTags: List<String>,
    val energyLevelTags: List<String>,
    val studyFriendlyTags: List<String>,
    val imageUrls: List<String>
)