package com.neu.mobileapplicationdevelopment202430.model

import com.google.gson.annotations.SerializedName

sealed class Product {
    abstract val id: String
    abstract val name: String
    abstract val category: String
    abstract val price: String
    abstract val imageUrl: String

    data class Food(
        @SerializedName("id") override val id: String,
        @SerializedName("name") override val name: String,
        @SerializedName("category") override val category: String,
        @SerializedName("expiryDate") val expiryDate: String?,
        @SerializedName("price") override val price: String,
        @SerializedName("imageUrl") override val imageUrl: String
    ) : Product()

    data class Equipment(
        @SerializedName("id") override val id: String,
        @SerializedName("name") override val name: String,
        @SerializedName("category") override val category: String,
        @SerializedName("warranty") val warranty: String?,
        @SerializedName("price") override val price: String,
        @SerializedName("imageUrl") override val imageUrl: String
    ) : Product()

    companion object {
        fun fromApiResponse(
            id: String,
            name: String,
            category: String,
            expiryDate: String?,
            price: String,
            warranty: String?,
            imageUrl: String
        ): Product {
            return when (category.lowercase()) {
                "food" -> Food(
                    id = id,
                    name = name,
                    category = category,
                    expiryDate = expiryDate,
                    price = price,
                    imageUrl = imageUrl
                )
                "equipment" -> Equipment(
                    id = id,
                    name = name,
                    category = category,
                    warranty = warranty,
                    price = price,
                    imageUrl = imageUrl
                )
                else -> throw IllegalArgumentException("Unknown category: $category")
            }
        }
    }
} 