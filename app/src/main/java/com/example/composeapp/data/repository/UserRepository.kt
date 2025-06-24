package com.example.composeapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.composeapp.data.network.*
import com.example.composeapp.data.repository.interfaces.UserRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val apiService: ApiService) : UserRepositoryInterface {
    
    // Login user
    override fun loginUser(username: String, password: String): LiveData<ApiResult<LoginResponse>> = liveData {
        emit(safeApiCall("User Login") { 
            apiService.login(UserLogin(username, password))
        })
    }
    
    // Create new user
    override fun createUser(
        name: String,
        password: String,
        cafesVisited: Int,
        averageRating: Double
    ): LiveData<ApiResult<UserResponse>> = liveData {
        emit(safeApiCall("Create User") {
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
    override fun getUserById(userId: String): LiveData<ApiResult<UserResponse>> = liveData {
        emit(safeApiCall("Get User by ID") { apiService.getUserById(userId) })
    }
    
    // Update user
    override suspend fun updateUser(userId: String, userUpdate: UserUpdate): ApiResult<UserResponse> {
        return withContext(Dispatchers.IO) {
            safeApiCall("Update User") { apiService.updateUser(userId, userUpdate) }
        }
    }
    
    // Search users
    override fun searchUsers(query: String): LiveData<ApiResult<List<UserResponse>>> = liveData {
        emit(safeApiCall("Search Users") { apiService.searchUsers(query) })
    }
    
    // Get all users
    override fun getAllUsers(skip: Int, limit: Int): LiveData<ApiResult<List<UserResponse>>> = liveData {
        emit(safeApiCall("Get All Users") { apiService.getAllUsers(skip, limit) })
    }
    
    // Delete user
    override suspend fun deleteUser(userId: String): ApiResult<Map<String, String>> {
        return withContext(Dispatchers.IO) {
            safeApiCall("Delete User") { apiService.deleteUser(userId) }
        }
    }
    
    // Get user bookmarks
    override fun getUserBookmarks(userId: String): LiveData<ApiResult<List<Cafe>>> = liveData {
        val result = safeApiCall("Get User Bookmarks") { apiService.getUserBookmarks(userId) }
        when (result) {
            is ApiResult.Success -> {
                // Extract cafe objects from bookmark response and convert to Cafe objects
                val cafes = result.data.mapNotNull { bookmark ->
                    bookmark.cafe?.toCafe()
                }
                emit(ApiResult.Success(cafes))
            }
            is ApiResult.Error -> {
                emit(result)
            }
        }
    }
    
    // Create bookmark
    override suspend fun createBookmark(userId: String, cafeId: String): ApiResult<Bookmark> {
        return withContext(Dispatchers.IO) {
            safeApiCall("Create Bookmark") { 
                apiService.createBookmark(BookmarkCreate(userId, cafeId))
            }
        }
    }
    
    // Delete bookmark
    override suspend fun deleteBookmark(userId: String, cafeId: String): ApiResult<Map<String, String>> {
        return withContext(Dispatchers.IO) {
            safeApiCall("Delete Bookmark") { 
                apiService.deleteUserCafeBookmark(userId, cafeId)
            }
        }
    }
    
    // Check if bookmark exists
    override suspend fun checkBookmarkExists(userId: String, cafeId: String): ApiResult<Map<String, Boolean>> {
        return withContext(Dispatchers.IO) {
            safeApiCall("Check Bookmark Exists") { 
                apiService.checkBookmarkExists(userId, cafeId)
            }
        }
    }
} 