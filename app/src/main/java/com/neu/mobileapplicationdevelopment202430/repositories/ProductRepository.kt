package com.neu.mobileapplicationdevelopment202430.repositories

import com.neu.mobileapplicationdevelopment202430.api.RetrofitClient
import com.neu.mobileapplicationdevelopment202430.database.AppDatabase
import com.neu.mobileapplicationdevelopment202430.database.ProductEntity
import com.neu.mobileapplicationdevelopment202430.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductRepository(private val database: AppDatabase) {
    private val apiService = RetrofitClient.apiService
    private val productDao = database.productDao()

    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
        .map { entities -> entities.map { it.toProduct() }.distinctBy { it.id } }

    fun getProductsByCategory(category: String): Flow<List<Product>> =
        productDao.getProductsByCategory(category)
            .map { entities -> entities.map { it.toProduct() }.distinctBy { it.id } }

    suspend fun refreshProducts() {
        try {
            val products = apiService.getProducts()
            productDao.insertProducts(products.map { ProductEntity.fromProduct(it) })
        } catch (e: Exception) {
            // If API call fails, we'll continue using cached data
            if (productDao.getAllProducts().map { it.isEmpty() }.equals(true)) {
                throw e // Only throw if we have no cached data
            }
        }
    }
}
