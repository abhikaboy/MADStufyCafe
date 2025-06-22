package com.example.composeapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.example.composeapp.data.network.*
import com.example.composeapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    
    // Current user state
    private val _currentUser = MutableLiveData<UserResponse?>()
    val currentUser: LiveData<UserResponse?> = _currentUser
    
    // Login state
    private val _loginCredentials = MutableLiveData<LoginCredentials>()
    val loginResult: LiveData<ApiResult<LoginResponse>> = _loginCredentials.switchMap { credentials ->
        userRepository.loginUser(credentials.username, credentials.password)
    }
    
    // User creation
    private val _userCreateData = MutableLiveData<UserCreateData>()
    val userCreateResult: LiveData<ApiResult<UserResponse>> = _userCreateData.switchMap { data ->
        userRepository.createUser(data.name, data.password, data.cafesVisited, data.averageRating)
    }
    
    // User search
    private val _searchQuery = MutableLiveData<String>()
    val searchResults: LiveData<ApiResult<List<UserResponse>>> = _searchQuery.switchMap { query ->
        userRepository.searchUsers(query)
    }
    
    // User bookmarks
    private val _userId = MutableLiveData<String>()
    val userBookmarks: LiveData<ApiResult<List<BookmarkWithCafe>>> = _userId.switchMap { userId ->
        userRepository.getUserBookmarks(userId)
    }
    
    // All users
    private val _usersParams = MutableLiveData<UsersParams>()
    val allUsers: LiveData<ApiResult<List<UserResponse>>> = _usersParams.switchMap { params ->
        userRepository.getAllUsers(params.skip, params.limit)
    }
    
    // Loading and error states
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    // Authentication state
    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn
    
    init {
        _isLoggedIn.value = false
        loadAllUsers()
    }
    
    fun login(username: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null
        _loginCredentials.value = LoginCredentials(username, password)
    }
    
    fun createUser(name: String, password: String, cafesVisited: Int = 0, averageRating: Double = 0.0) {
        _isLoading.value = true
        _errorMessage.value = null
        _userCreateData.value = UserCreateData(name, password, cafesVisited, averageRating)
    }
    
    fun searchUsers(query: String) {
        _searchQuery.value = query
    }
    
    fun loadUserBookmarks(userId: String) {
        _userId.value = userId
    }
    
    fun loadAllUsers(skip: Int = 0, limit: Int = 100) {
        _usersParams.value = UsersParams(skip, limit)
    }
    
    fun updateUser(userId: String, name: String? = null, cafesVisited: Int? = null, averageRating: Double? = null, password: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val userUpdate = UserUpdate(name, cafesVisited, averageRating, password)
                val result = userRepository.updateUser(userId, userUpdate)
                
                when (result) {
                    is ApiResult.Success -> {
                        _currentUser.value = result.data
                        _errorMessage.value = null
                    }
                    is ApiResult.Error -> {
                        _errorMessage.value = result.message
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Unknown error occurred"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            try {
                val result = userRepository.deleteUser(userId)
                when (result) {
                    is ApiResult.Success -> {
                        _errorMessage.value = null
                        // Refresh users list
                        loadAllUsers()
                    }
                    is ApiResult.Error -> {
                        _errorMessage.value = result.message
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = e.localizedMessage ?: "Failed to delete user"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun createBookmark(userId: String, cafeId: String) {
        viewModelScope.launch {
            try {
                val result = userRepository.createBookmark(userId, cafeId)
                when (result) {
                    is ApiResult.Success -> {
                        // Refresh bookmarks
                        loadUserBookmarks(userId)
                    }
                    is ApiResult.Error -> {
                        _errorMessage.value = "Failed to create bookmark: ${result.message}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to create bookmark: ${e.localizedMessage}"
            }
        }
    }
    
    fun deleteBookmark(userId: String, cafeId: String) {
        viewModelScope.launch {
            try {
                val result = userRepository.deleteBookmark(userId, cafeId)
                when (result) {
                    is ApiResult.Success -> {
                        // Refresh bookmarks
                        loadUserBookmarks(userId)
                    }
                    is ApiResult.Error -> {
                        _errorMessage.value = "Failed to delete bookmark: ${result.message}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to delete bookmark: ${e.localizedMessage}"
            }
        }
    }
    
    fun checkBookmarkExists(userId: String, cafeId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val result = userRepository.checkBookmarkExists(userId, cafeId)
                when (result) {
                    is ApiResult.Success -> {
                        val exists = result.data["exists"] ?: false
                        onResult(exists)
                    }
                    is ApiResult.Error -> {
                        _errorMessage.value = "Failed to check bookmark: ${result.message}"
                        onResult(false)
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to check bookmark: ${e.localizedMessage}"
                onResult(false)
            }
        }
    }
    
    fun logout() {
        _currentUser.value = null
        _isLoggedIn.value = false
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
    
    // Handle login result
    fun handleLoginResult(result: ApiResult<LoginResponse>) {
        when (result) {
            is ApiResult.Success -> {
                _isLoggedIn.value = true
                _errorMessage.value = null
                // You might want to save user ID for future use
            }
            is ApiResult.Error -> {
                _isLoggedIn.value = false
                _errorMessage.value = result.message
            }
        }
        _isLoading.value = false
    }
    
    // Handle user creation result
    fun handleUserCreateResult(result: ApiResult<UserResponse>) {
        when (result) {
            is ApiResult.Success -> {
                _currentUser.value = result.data
                _errorMessage.value = null
            }
            is ApiResult.Error -> {
                _errorMessage.value = result.message
            }
        }
        _isLoading.value = false
    }
}

// Helper data classes
data class LoginCredentials(
    val username: String,
    val password: String
)

data class UserCreateData(
    val name: String,
    val password: String,
    val cafesVisited: Int,
    val averageRating: Double
)

data class UsersParams(
    val skip: Int,
    val limit: Int
) 