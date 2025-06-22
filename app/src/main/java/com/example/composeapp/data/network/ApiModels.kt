package com.example.composeapp.data.network

import com.example.composeapp.data.database.CafeEntity
import com.google.gson.annotations.SerializedName

// User models
data class UserResponse(
    @SerializedName("_id") val id: String,
    val name: String,
    val cafes_visited: Int = 0,
    val average_rating: Double = 0.0,
    val profile_picture: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)

data class UserCreate(
    val name: String,
    val password: String,
    val cafes_visited: Int = 0,
    val average_rating: Double = 0.0
)

data class UserLogin(
    val name: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val user_id: String,
    val user_name: String
)

// Address model
data class Address(
    val street: String,
    val city: String,
    val state: String,
    val zip_code: String,
    val country: String = "USA"
)

// Location model for GeoJSON
data class Location(
    val type: String = "Point",
    val coordinates: List<Double> // [longitude, latitude]
)

// Cafe models
data class Cafe(
    @SerializedName("_id") val id: String,
    val name: String,
    val address: Address,
    val location: Location,
    val phone: String? = null,
    val website: String? = null,
    val opening_hours: Map<String, String>? = null,
    val amenities: List<String>? = null,
    val thumbnail_url: String? = null,
    val wifi_access: Int = 0, // AccessLevel 0-3
    val outlet_accessibility: Int = 0, // AccessLevel 0-3
    val average_rating: Int = 1,
    val created_at: String? = null,
    val updated_at: String? = null
)

data class CafeCreate(
    val name: String,
    val address: Address,
    val location: Location,
    val phone: String? = null,
    val website: String? = null,
    val opening_hours: Map<String, String>? = null,
    val amenities: List<String>? = null,
    val thumbnail_url: String? = null,
    val wifi_access: Int = 0,
    val outlet_accessibility: Int = 0,
    val average_rating: Int = 1
)

// Review models
data class Review(
    @SerializedName("_id") val id: String,
    val study_spot_id: String,
    val user_id: String,
    val overall_rating: Double,
    val outlet_accessibility: Double,
    val wifi_quality: Double,
    val atmosphere: String? = null,
    val energy_level: String? = null,
    val study_friendly: String? = null,
    val photos: List<Photo> = emptyList(),
    val created_at: String? = null,
    val updated_at: String? = null
)

data class ReviewCreate(
    val study_spot_id: String,
    val user_id: String,
    val overall_rating: Double,
    val outlet_accessibility: Double,
    val wifi_quality: Double,
    val atmosphere: String? = null,
    val energy_level: String? = null,
    val study_friendly: String? = null
)

data class Photo(
    @SerializedName("_id") val id: String,
    val url: String,
    val caption: String? = null
)

// Bookmark models
data class Bookmark(
    @SerializedName("_id") val id: String,
    val user_id: String,
    val cafe_id: String,
    val bookmarked_at: String
)

data class BookmarkCreate(
    val user_id: String,
    val cafe_id: String
)

data class BookmarkWithCafe(
    @SerializedName("_id") val id: String,
    val user_id: String,
    val cafe_id: String,
    val bookmarked_at: String,
    val cafe: Cafe? = null
)

// File upload response
data class FileUploadResponse(
    val url: String,
    val message: String
)

// Extension functions to convert API models to Room entities
fun Cafe.toEntity(): CafeEntity {
    return CafeEntity(
        id = id.hashCode().toLong(), // Convert string ID to long for Room
        name = name,
        address = "${address.street}, ${address.city}, ${address.state} ${address.zip_code}",
        description = "",
        latitude = if (location.coordinates.size >= 2) location.coordinates[1] else null,
        longitude = if (location.coordinates.size >= 2) location.coordinates[0] else null,
        phone = phone,
        website = website,
        hours = opening_hours?.entries?.joinToString(", ") { "${it.key}: ${it.value}" },
        priceRange = null,
        tags = amenities?.joinToString(",") ?: "",
        studyRating = average_rating,
        ambianceRating = average_rating,
        wifiQuality = when (wifi_access) {
            0 -> "None"
            1 -> "Poor"
            2 -> "Good"
            3 -> "Excellent"
            else -> "Unknown"
        },
        powerOutlets = when (outlet_accessibility) {
            0 -> "None"
            1 -> "Limited"
            2 -> "Some"
            3 -> "Many"
            else -> "Unknown"
        },
        noiseLevel = "Medium",
        seatingCapacity = null,
        hasParking = amenities?.any { it.contains("parking", ignoreCase = true) } ?: false,
        hasFood = true,
        hasCoffee = true,
        hasStudyArea = true,
        imageUrl = thumbnail_url ?: "",
        thumbnailUrl = thumbnail_url ?: "",
        atmosphereTags = "",
        energyLevelTags = "",
        studyFriendlyTags = "",
        ratingImageUrls = "",
        createdAt = created_at,
        updatedAt = updated_at,
        isBookmarked = false
    )
} 