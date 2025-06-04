package com.neu.mobileapplicationdevelopment202430.repositories

import com.neu.mobileapplicationdevelopment202430.api.RetrofitClient
import android.util.Log
import com.neu.mobileapplicationdevelopment202430.database.AppDatabase
import com.neu.mobileapplicationdevelopment202430.database.ProductEntity
import com.neu.mobileapplicationdevelopment202430.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

private const val TAG = "ProductRepository"

class ProductRepository(private val database: AppDatabase) {
    private val apiService = RetrofitClient.apiService
    private val productDao = database.productDao()

    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
        .onEach { entities -> 
            Log.d(TAG, "Database returned ${entities.size} products")
            entities.forEach { entity ->
                Log.d(TAG, "Product from DB: ${entity.name}")
            }
        }
        .map { entities -> 
            entities.map { it.toProduct() }.distinctBy { it.id }.also { products ->
                Log.d(TAG, "Mapped to ${products.size} distinct products")
            }
        }

    suspend fun refreshProducts() {
        try {
            Log.d(TAG, "Starting API request to fetch products")
            val products = apiService.getProducts()
            Log.d(TAG, "API returned ${products.size} products")
            products.forEach { product ->
                Log.d(TAG, "Product from API: ${product.name} (${product::class.simpleName})")
            }
            
            val entities = products.map { ProductEntity.fromProduct(it) }
            Log.d(TAG, "Inserting ${entities.size} products into database")
            productDao.insertProducts(entities)
            Log.d(TAG, "Successfully inserted products into database")
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching products", e)
            // If API call fails, we'll continue using cached data
            val cachedProducts = productDao.getAllProducts()
            Log.d(TAG, "Cached products count: ${cachedProducts}")
            if (productDao.getAllProducts().map { it.isEmpty() }.equals(true)) {
                Log.e(TAG, "No cached data available, throwing error")
                throw e // Only throw if we have no cached data
            }
        }
    }
}
