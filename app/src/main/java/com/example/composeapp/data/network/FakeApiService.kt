package com.example.composeapp.data.network

import okhttp3.MultipartBody

class FakeApiService: ApiService {
    private val users = mutableListOf<UserResponse>()
    private val cafes = mutableListOf<Cafe>()
    private val reviews = mutableListOf<Review>()
    private val bookmarks = mutableListOf<Bookmark>()

    override suspend fun createUser(user: UserCreate): UserResponse {
        val newUser = UserResponse(
            id = (users.size + 1).toString(),
            name = user.name,
            cafes_visited = 2,
            average_rating = 3.0
        )
        users.add(newUser)
        return newUser
    }

    override suspend fun getAllUsers(
        skip: Int,
        limit: Int
    ): List<UserResponse> {
        return users.drop(skip).take(limit)
    }

    override suspend fun getUserById(userId: String): UserResponse {
        return users.find { it.id == userId }
            ?: throw IllegalArgumentException("User not found")
    }

    override suspend fun updateUser(
        userId: String,
        user: UserUpdate
    ): UserResponse {
        val index = users.indexOfFirst { it.id == userId }
        if (index == -1) throw IllegalArgumentException("User not found")
        val current = users[index]
        val updated = current.copy(
            name = user.name ?: current.name,
            cafes_visited = user.cafes_visited ?: current.cafes_visited,
            average_rating = user.average_rating ?: current.average_rating
            // profile_picture, created_at, updated_at left unchanged
        )
        users[index] = updated
        return updated
    }

    override suspend fun deleteUser(userId: String): Map<String, String> {
        val removed = users.removeIf { it.id == userId }
        return mapOf("status" to if (removed) "success" else "not_found")
    }

    override suspend fun searchUsers(query: String): List<UserResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun login(credentials: UserLogin): LoginResponse {
        return LoginResponse(
            message = "Login successful",
            user_id = "123",
            user_name = credentials.name
        )
    }

    override suspend fun uploadProfilePicture(
        userId: String,
        file: MultipartBody.Part
    ): FileUploadResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getAllCafes(): List<Cafe> {
        return cafes
    }

    override suspend fun createCafe(cafe: CafeCreate): Cafe {
        var nextId = 0
        val newCafe = Cafe(
            id = "${nextId++}",
            name = cafe.name,
            address = cafe.address,
            location = null,
            wifi_access = 0,
            outlet_accessibility = 2,
            average_rating = 1
        )
        cafes.add(newCafe)
        return newCafe
    }

    override suspend fun getCafeById(cafeId: String): Cafe {
        return cafes.find { it.id.toString() == cafeId }
            ?: throw IllegalArgumentException("Cafe not found")
    }

    override suspend fun updateCafe(
        cafeId: String,
        cafe: CafeUpdate
    ): Cafe {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCafe(cafeId: String): Map<String, String> {
        TODO("Not yet implemented")
    }

    override suspend fun searchCafes(query: String): List<Cafe> {
        TODO("Not yet implemented")
    }

    override suspend fun findNearbyCafes(
        longitude: Double,
        latitude: Double,
        maxDistance: Double
    ): List<Cafe> {
        TODO("Not yet implemented")
    }

    override suspend fun findCafesByAmenities(amenities: List<String>): List<Cafe> {
        TODO("Not yet implemented")
    }

    override suspend fun findCafesByRating(minRating: Double): List<Cafe> {
        TODO("Not yet implemented")
    }

    override suspend fun getCafePhotos(cafeId: String): List<Map<String, Any>> {
        TODO("Not yet implemented")
    }

    override suspend fun createReview(review: ReviewCreate): Review {
        TODO("Not yet implemented")
    }

    override suspend fun getReviewById(reviewId: String): Review {
        TODO("Not yet implemented")
    }

    override suspend fun updateReview(
        reviewId: String,
        review: ReviewUpdate
    ): Review {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReview(reviewId: String): Map<String, String> {
        TODO("Not yet implemented")
    }

    override suspend fun getReviewsByStudySpot(studySpotId: String): List<Review> {
        TODO("Not yet implemented")
    }

    override suspend fun getReviewsByUser(userId: String): List<Review> {
        TODO("Not yet implemented")
    }

    override suspend fun addPhotoToReview(
        reviewId: String,
        photo: PhotoCreate
    ): Review {
        TODO("Not yet implemented")
    }

    override suspend fun uploadPhotoToReview(
        reviewId: String,
        file: MultipartBody.Part,
        caption: String
    ): Review {
        TODO("Not yet implemented")
    }

    override suspend fun createBookmark(bookmark: BookmarkCreate): Bookmark {
        var nextId = 0
        val newBookmark = Bookmark(
            id = "${nextId++}",
            user_id = "${bookmark.user_id}",
            cafe_id = "${bookmark.cafe_id}",
            bookmarked_at = "None"
        )
        bookmarks.add(newBookmark)
        return newBookmark
    }

    override suspend fun getBookmarkById(bookmarkId: String): Bookmark {
        return bookmarks.find { bookmarkId == it.id }
            ?: throw IllegalArgumentException("Cafe not found")
    }

    override suspend fun deleteBookmarkById(bookmarkId: String): Map<String, String> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUserBookmark(
        userId: String,
        studySpotId: String
    ): Map<String, String> {
        TODO("Not yet implemented")
    }

    override suspend fun getUserBookmarks(userId: String): List<BookmarkWithCafe> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteUserCafeBookmark(
        userId: String,
        cafeId: String
    ): Map<String, String> {
        TODO("Not yet implemented")
    }

    override suspend fun checkBookmarkExists(
        userId: String,
        cafeId: String
    ): Map<String, Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadFile(file: MultipartBody.Part): FileUploadResponse {
        TODO("Not yet implemented")
    }

    override suspend fun uploadMultipleFiles(files: List<MultipartBody.Part>): Map<String, Any> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFile(fileUrl: String): Map<String, String> {
        TODO("Not yet implemented")
    }

    override suspend fun getFileInfo(fileUrl: String): Map<String, Any> {
        TODO("Not yet implemented")
    }

    override suspend fun apiRoot(): Map<String, Any> {
        TODO("Not yet implemented")
    }

    override suspend fun healthCheck(): Map<String, Any> {
        TODO("Not yet implemented")
    }

}