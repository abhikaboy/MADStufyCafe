package com.example.composeapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.composeapp.data.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val apiService: ApiService) {
    
    // Login user
    fun loginUser(username: String, password: String): LiveData<ApiResult<LoginResponse>> = liveData {
        emit(safeApiCall { 
            apiService.login(UserLogin(username, password))
        })
    }
    
    // Create new user
    fun createUser(
        name: String, 
        password: String, 
        cafesVisited: Int = 0, 
        averageRating: Double = 0.0
    ): LiveData<ApiResult<UserResponse>> = liveData {
        emit(safeApiCall {
            apiService.createUser(
                UserCreate(
                    name = name,
                    password = password,
                    cafes_visited = cafesVisited,
                    average_rating = averageRating
                )
            )
        })
    }
    
    // Get user by ID
    fun getUserById(userId: String): LiveData<ApiResult<UserResponse>> = liveData {
        emit(safeApiCall { apiService.getUserById(userId) })
    }
    
    // Update user
    suspend fun updateUser(userId: String, userUpdate: UserUpdate): ApiResult<UserResponse> {
        return withContext(Dispatchers.IO) {
            safeApiCall { apiService.updateUser(userId, userUpdate) }
        }
    }
    
    // Search users
    fun searchUsers(query: String): LiveData<ApiResult<List<UserResponse>>> = liveData {
        emit(safeApiCall { apiService.searchUsers(query) })
    }
    
    // Get all users
    fun getAllUsers(skip: Int = 0, limit: Int = 100): LiveData<ApiResult<List<UserResponse>>> = liveData {
        emit(safeApiCall { apiService.getAllUsers(skip, limit) })
    }
    
    // Delete user
    suspend fun deleteUser(userId: String): ApiResult<Map<String, String>> {
        return withContext(Dispatchers.IO) {
            safeApiCall { apiService.deleteUser(userId) }
        }
    }
    
    // Get user bookmarks
    fun getUserBookmarks(userId: String): LiveData<ApiResult<List<BookmarkWithCafe>>> = liveData {
        emit(safeApiCall { apiService.getUserBookmarks(userId) })
    }
    
    // Create bookmark
    suspend fun createBookmark(userId: String, cafeId: String): ApiResult<Bookmark> {
        return withContext(Dispatchers.IO) {
            safeApiCall { 
                apiService.createBookmark(BookmarkCreate(userId, cafeId))
            }
        }
    }
    
    // Delete bookmark
    suspend fun deleteBookmark(userId: String, cafeId: String): ApiResult<Map<String, String>> {
        return withContext(Dispatchers.IO) {
            safeApiCall { 
                apiService.deleteUserCafeBookmark(userId, cafeId)
            }
        }
    }
    
    // Check if bookmark exists
    suspend fun checkBookmarkExists(userId: String, cafeId: String): ApiResult<Map<String, Boolean>> {
        return withContext(Dispatchers.IO) {
            safeApiCall { 
                apiService.checkBookmarkExists(userId, cafeId)
            }
        }
    }
} 