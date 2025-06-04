package com.neu.mobileapplicationdevelopment202430.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.neu.mobileapplicationdevelopment202430.database.AppDatabase
import com.neu.mobileapplicationdevelopment202430.model.Product
import com.neu.mobileapplicationdevelopment202430.repositories.ProductRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ProductRepository
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    private val _selectedCategory = MutableStateFlow<String?>(null)

    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val error: StateFlow<String?> = _error.asStateFlow()
    val products: StateFlow<List<Product>> 

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ProductRepository(database)
        products = combine(
            repository.allProducts,
            _selectedCategory
        ) { products, category ->
            when (category) {
                null -> products
                else -> products.filter { it.category.lowercase() == category.lowercase() }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                repository.refreshProducts()
            } catch (e: Exception) {
                _error.value = e.message ?: "An error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }
} 