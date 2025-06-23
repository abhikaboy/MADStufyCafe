package com.example.composeapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.data.network.ApiResult
import com.example.composeapp.data.network.LoginResponse
import com.example.composeapp.data.network.UserResponse
import com.example.composeapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    
    // UI State
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    private val _isLoggedIn = MutableLiveData(false)
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn
    
    private val _currentUser = MutableLiveData<UserResponse?>()
    val currentUser: LiveData<UserResponse?> = _currentUser
    
    fun login(username: String, password: String) {
        if (username.isBlank()) {
            _errorMessage.value = "Please enter a username"
            return
        }
        
        if (password.isBlank()) {
            _errorMessage.value = "Please enter a password"
            return
        }
        
        _isLoading.value = true
        _errorMessage.value = null
        
        // Observe the login result
        val loginLiveData = userRepository.loginUser(username.trim(), password)
        loginLiveData.observeForever { result ->
            when (result) {
                is ApiResult.Success -> {
                    // After successful login, fetch full user details
                    fetchUserDetails(result.data.user_id)
                    // Remove observer after success
                    loginLiveData.removeObserver { }
                }
                is ApiResult.Error -> {
                    _isLoading.value = false
                    _errorMessage.value = result.message
                    _isLoggedIn.value = false
                    _currentUser.value = null
                    // Remove observer after error
                    loginLiveData.removeObserver { }
                }
            }
        }
    }
    
    private fun fetchUserDetails(userId: String) {
        val userDetailsLiveData = userRepository.getUserById(userId)
        userDetailsLiveData.observeForever { result ->
            _isLoading.value = false
            when (result) {
                is ApiResult.Success -> {
                    _currentUser.value = result.data
                    _isLoggedIn.value = true
                    _errorMessage.value = null
                }
                is ApiResult.Error -> {
                    _errorMessage.value = "Failed to load user details: ${result.message}"
                    _isLoggedIn.value = false
                    _currentUser.value = null
                }
            }
            // Remove observer after handling result
            userDetailsLiveData.removeObserver { }
        }
    }
    
    fun logout() {
        _isLoggedIn.value = false
        _currentUser.value = null
        clearError()
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    // Check if user is logged in (you might want to persist this in SharedPreferences)
    fun checkLoginStatus() {
        // For now, we'll just check if we have a current user
        // In a real app, you'd check saved authentication tokens
        _isLoggedIn.value = _currentUser.value != null
    }
} 