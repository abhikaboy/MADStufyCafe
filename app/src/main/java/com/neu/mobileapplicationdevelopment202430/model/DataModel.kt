package com.neu.mobileapplicationdevelopment202430

import com.google.gson.annotations.SerializedName

sealed data class Product(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("category") val category: String,
    @SerializedName("expiryDate") val expiryDate: String?,
    @SerializedName("price") val price: String,
    @SerializedName("warranty") val warranty: String?,
    @SerializedName("imageUrl") val imageUrl: String
) {
    fun isFood() = category.lowercase() == "food"
    fun isEquipment() = category.lowercase() == "equipment"
    
    fun getImageResource(): Int {
        return if (isFood()) R.drawable.food else R.drawable.equipment
    }
} 

data class ProductResponse(
    @SerializedName("products") val products: List<Product>
)

