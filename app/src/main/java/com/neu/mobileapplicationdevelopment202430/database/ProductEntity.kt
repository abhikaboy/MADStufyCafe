package com.neu.mobileapplicationdevelopment202430.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.neu.mobileapplicationdevelopment202430.model.Product

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String,
    val price: String,
    val imageUrl: String,
    val productType: String, // "food" or "equipment"
    val expiryDate: String?, // Only for food products
    val warranty: String?    // Only for equipment products
) {
    fun toProduct(): Product {
        return when (productType) {
            "food" -> Product.Food(
                id = id,
                name = name,
                category = category,
                expiryDate = expiryDate,
                price = price,
                imageUrl = imageUrl
            )
            "equipment" -> Product.Equipment(
                id = id,
                name = name,
                category = category,
                warranty = warranty,
                price = price,
                imageUrl = imageUrl
            )
            else -> throw IllegalArgumentException("Unknown product type: $productType")
        }
    }

    companion object {
        fun fromProduct(product: Product): ProductEntity {
            return when (product) {
                is Product.Food -> ProductEntity(
                    id = product.id,
                    name = product.name,
                    category = product.category,
                    price = product.price,
                    imageUrl = product.imageUrl,
                    productType = "food",
                    expiryDate = product.expiryDate,
                    warranty = null
                )
                is Product.Equipment -> ProductEntity(
                    id = product.id,
                    name = product.name,
                    category = product.category,
                    price = product.price,
                    imageUrl = product.imageUrl,
                    productType = "equipment",
                    expiryDate = null,
                    warranty = product.warranty
                )
            }
        }
    }
} 