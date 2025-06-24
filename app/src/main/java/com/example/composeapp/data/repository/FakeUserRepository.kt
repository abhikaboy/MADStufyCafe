package com.example.composeapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.composeapp.data.network.*

class FakeUserRepository : UserRepositoryInterface {

    // Tracking flags
    var loginWasCalled = false
    var createUserWasCalled = false
    var getUserByIdWasCalled = false
    var updateUserWasCalled = false
    var searchUsersWasCalled = false
    var createBookmarkWasCalled = false
    var deleteBookmarkWasCalled = false
    var checkBookmarkExistsWasCalled = false
    var getUserBookmarksWasCalled = false
    var deleteUserWasCalled = false
    var getAllUsersWasCalled = false
    var lastDeleteUserId: String? = null
    var lastGetAllUsersSkip: Int? = null
    var lastGetAllUsersLimit: Int? = null
    var lastSearchQuery: String? = null



    // Private backing properties to avoid conflicts
    private var _loginResult: ApiResult<LoginResponse>? = null
    private var _userResult: ApiResult<UserResponse>? = null
    private var _updateUserResult: ApiResult<UserResponse>? = null
    private var _createBookmarkResult: ApiResult<Bookmark>? = null
    private var _deleteUserResult: ApiResult<UserResponse>? = null
    private var _searchUsersResult: ApiResult<List<UserResponse>>? = null
    private var _getAllUsersResult: ApiResult<List<UserResponse>>? = null


    override fun loginUser(username: String, password: String): LiveData<ApiResult<LoginResponse>> {
        loginWasCalled = true
        return MutableLiveData(_loginResult ?: ApiResult.Error("Not configured"))
    }

    override fun createUser(
        name: String,
        password: String,
        cafesVisited: Int,
        averageRating: Double
    ): LiveData<ApiResult<UserResponse>> {
        createUserWasCalled = true
        return MutableLiveData(_userResult ?: ApiResult.Error("Not configured"))
    }

    override fun getUserById(userId: String): LiveData<ApiResult<UserResponse>> {
        getUserByIdWasCalled = true
        return MutableLiveData(_userResult ?: ApiResult.Error("Not configured"))
    }

    override suspend fun updateUser(userId: String, userUpdate: UserUpdate): ApiResult<UserResponse> {
        updateUserWasCalled = true
        return _updateUserResult ?: ApiResult.Error("Not configured")
    }

    override suspend fun deleteUser(userId: String): ApiResult<Map<String, String>> {
        deleteUserWasCalled = true
        lastDeleteUserId = userId
        val defaultResult: Map<String, String> = mapOf("status" to "success")
        return (_deleteUserResult ?: ApiResult.Success(defaultResult)) as ApiResult<Map<String, String>>
    }

    override fun searchUsers(query: String): LiveData<ApiResult<List<UserResponse>>> {
        searchUsersWasCalled = true
        lastSearchQuery = query
        return MutableLiveData(ApiResult.Success(emptyList()))
    }

    override fun getAllUsers(skip: Int, limit: Int): LiveData<ApiResult<List<UserResponse>>> {
        getAllUsersWasCalled = true
        lastGetAllUsersSkip = skip
        lastGetAllUsersLimit = limit
        return MutableLiveData(_getAllUsersResult ?: ApiResult.Success(emptyList()))
    }

    override suspend fun createBookmark(userId: String, cafeId: String): ApiResult<Bookmark> {
        createBookmarkWasCalled = true
        return _createBookmarkResult ?: ApiResult.Error("Not configured")
    }

    override suspend fun deleteBookmark(userId: String, cafeId: String): ApiResult<Map<String, String>> {
        deleteBookmarkWasCalled = true
        return ApiResult.Success(mapOf("status" to "success"))
    }

    override suspend fun checkBookmarkExists(userId: String, cafeId: String): ApiResult<Map<String, Boolean>> {
        checkBookmarkExistsWasCalled = true
        return ApiResult.Success(mapOf("exists" to true))
    }

    override fun getUserBookmarks(userId: String): LiveData<ApiResult<List<Cafe>>> {
        getUserBookmarksWasCalled = true
        return MutableLiveData(ApiResult.Success(emptyList()))
    }

    // Setter functions that modify the private backing properties
    fun configureLoginResult(result: ApiResult<LoginResponse>?) {
        _loginResult = result
    }

    fun configureUserResult(result: ApiResult<UserResponse>?) {
        _userResult = result
    }

    fun configureUpdateUserResult(result: ApiResult<UserResponse>?) {
        _updateUserResult = result
    }

    fun configureCreateBookmarkResult(result: ApiResult<Bookmark>?) {
        _createBookmarkResult = result
    }

    fun resetTrackingFlags() {
        loginWasCalled = false
        createUserWasCalled = false
        getUserByIdWasCalled = false
        updateUserWasCalled = false
        searchUsersWasCalled = false
        createBookmarkWasCalled = false
        deleteBookmarkWasCalled = false
        checkBookmarkExistsWasCalled = false
        getUserBookmarksWasCalled = false
    }
}