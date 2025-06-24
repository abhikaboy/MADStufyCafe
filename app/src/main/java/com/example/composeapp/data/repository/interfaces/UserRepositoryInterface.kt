package com.example.composeapp.data.repository.interfaces

import androidx.lifecycle.LiveData
import com.example.composeapp.data.network.ApiResult
import com.example.composeapp.data.network.Bookmark
import com.example.composeapp.data.network.Cafe
import com.example.composeapp.data.network.LoginResponse
import com.example.composeapp.data.network.UserResponse
import com.example.composeapp.data.network.UserUpdate

interface UserRepositoryInterface {
    fun loginUser(username: String, password: String): LiveData<ApiResult<LoginResponse>>

    fun createUser(
        name: String,
        password: String,
        cafesVisited: Int = 0,
        averageRating: Double = 0.0
    ): LiveData<ApiResult<UserResponse>>

    fun getUserById(userId: String): LiveData<ApiResult<UserResponse>>

    suspend fun updateUser(userId: String, userUpdate: UserUpdate): ApiResult<UserResponse>

    suspend fun deleteUser(userId: String): ApiResult<Map<String, String>>

    fun searchUsers(query: String): LiveData<ApiResult<List<UserResponse>>>

    fun getAllUsers(skip: Int = 0, limit: Int = 100): LiveData<ApiResult<List<UserResponse>>>

    fun getUserBookmarks(userId: String): LiveData<ApiResult<List<Cafe>>>

    suspend fun createBookmark(userId: String, cafeId: String): ApiResult<Bookmark>

    suspend fun deleteBookmark(userId: String, cafeId: String): ApiResult<Map<String, String>>

    suspend fun checkBookmarkExists(userId: String, cafeId: String): ApiResult<Map<String, Boolean>>
}