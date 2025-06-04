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
    val expiryDate: String?,
    val price: String,
    val warranty: String?,
    val imageUrl: String
) {
    fun toProduct(): Product {
        return Product.fromApiResponse(
            id = id,
            name = name,
            category = category,
            expiryDate = expiryDate,
            price = price,
            warranty = warranty,
            imageUrl = imageUrl
        )
    }

    companion object {
        fun fromProduct(product: Product): ProductEntity {
            return ProductEntity(
                id = product.id,
                name = product.name,
                category = product.category,
                expiryDate = when (product) {
                    is Product.Food -> product.expiryDate
                    is Product.Equipment -> null
                },
                price = product.price,
                warranty = when (product) {
                    is Product.Equipment -> product.warranty
                    is Product.Food -> null
                },
                imageUrl = product.imageUrl
            )
        }
    }
} 