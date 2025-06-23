package com.example.composeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.composeapp.data.repository.CafeRepository
import com.example.composeapp.data.repository.UserRepository
import com.example.composeapp.data.repository.ReviewRepository

class ViewModelFactory(
    private val cafeRepository: CafeRepository,
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(CafeViewModel::class.java) -> {
                CafeViewModel(cafeRepository) as T
            }
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(userRepository, reviewRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(ReviewViewModel::class.java) -> {
                ReviewViewModel(reviewRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
} 