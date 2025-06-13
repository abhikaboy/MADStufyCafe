package com.example.composeapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cafes")
data class CafeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val tags: String, // Stored as comma-separated values
    val address: String,
    val imageUrl: String
) 