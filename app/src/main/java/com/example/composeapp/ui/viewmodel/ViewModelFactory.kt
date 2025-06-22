package com.example.composeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.composeapp.data.repository.CafeRepository
import com.example.composeapp.data.repository.UserRepository

class ViewModelFactory(
    private val cafeRepository: CafeRepository,
    private val userRepository: UserRepository? = null
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CafeViewModel::class.java) -> {
                CafeViewModel(cafeRepository) as T
            }
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                requireNotNull(userRepository) { "UserRepository is required for UserViewModel" }
                UserViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
} 