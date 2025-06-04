package com.neu.mobileapplicationdevelopment202430

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
    ) : Product() {
        override fun getImageResource(): Int = R.drawable.food
    }

    data class Equipment(
        @SerializedName("id") override val id: String,
        @SerializedName("name") override val name: String,
        @SerializedName("category") override val category: String,
        @SerializedName("warranty") val warranty: String?,
        @SerializedName("price") override val price: String,
        @SerializedName("imageUrl") override val imageUrl: String
    ) : Product() {
        override fun getImageResource(): Int = R.drawable.equipment
    }

    abstract fun getImageResource(): Int

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

data class ProductResponse(
    @SerializedName("products") val products: List<Product>
)

