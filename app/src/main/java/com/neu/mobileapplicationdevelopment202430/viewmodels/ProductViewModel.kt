package com.neu.mobileapplicationdevelopment202430.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.neu.mobileapplicationdevelopment202430.database.AppDatabase
import com.neu.mobileapplicationdevelopment202430.model.Product
import com.neu.mobileapplicationdevelopment202430.repositories.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val TAG = "ProductViewModel"

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductRepository
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _isAscending = MutableStateFlow(true)

    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val error: StateFlow<String?> = _error.asStateFlow()
    val products: StateFlow<List<Product>> 

    init {
        Log.d(TAG, "Initializing ProductViewModel")
        val database = AppDatabase.getDatabase(application)
        repository = ProductRepository(database)
        products = combine(
            repository.allProducts,
            _isAscending
        ) { products, isAscending ->
            Log.d(TAG, "Combining flows: ${products.size} products, isAscending: $isAscending")
            products.sortedBy { if (isAscending) it.name else it.name }.let { 
                if (!isAscending) it.reversed() else it 
            }.also { sortedProducts ->
                Log.d(TAG, "Sorted products: ${sortedProducts.size}")
                sortedProducts.forEach { product ->
                    Log.d(TAG, "Product after sorting: ${product.name}")
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    fun loadProducts() {
        Log.d(TAG, "Loading products")
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d(TAG, "Calling repository to refresh products")
                repository.refreshProducts()
                Log.d(TAG, "Successfully refreshed products")
            } catch (e: Exception) {
                Log.e(TAG, "Error loading products", e)
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
                Log.d(TAG, "Finished loading products")
            }
        }
    }

    fun setSortOrder(isAscending: Boolean) {
        Log.d(TAG, "Setting sort order: isAscending = $isAscending")
        _isAscending.value = isAscending
    }
} 