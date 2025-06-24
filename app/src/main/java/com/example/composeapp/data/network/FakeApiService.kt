package com.example.composeapp.data.network

import okhttp3.MultipartBody
import java.util.concurrent.atomic.AtomicInteger

class FakeApiService : ApiService {
    private val users = mutableListOf<UserResponse>()
    private val cafes = mutableListOf<Cafe>()
    private val reviews = mutableListOf<Review>()
    private val bookmarks = mutableListOf<Bookmark>()

    private val userIdGenerator = AtomicInteger(1)
    private val cafeIdGenerator = AtomicInteger(1)
    private val reviewIdGenerator = AtomicInteger(1)
    private val bookmarkIdGenerator = AtomicInteger(1)

    init {
        populateTestData()
    }

    private fun populateTestData() {
        users.addAll(listOf(
            UserResponse(
                id = "1",
                name = "Test User 1",
                cafes_visited = 5,
                average_rating = 4.2,
                created_at = "2024-01-01T00:00:00Z"
            ),
            UserResponse(
                id = "2",
                name = "Test User 2",
                cafes_visited = 3,
                average_rating = 3.8,
                created_at = "2024-01-02T00:00:00Z"
            )
        ))

        cafes.addAll(listOf(
            Cafe(
                id = "1",
                name = "The Productive Cup",
                address = Address(
                    street = "082 Barbara Ferry",
                    city = "Palo Alto",
                    state = "CA",
                    zip_code = "41855"
                ),
                location = Location(
                    coordinates = listOf(-122.02845225960077, 37.78502907244787)
                ),
                phone = "001-590-586-9810x696",
                website = "https://www.theproductivecup.com",
                amenities = listOf("Free WiFi", "Power outlets", "Quiet zone"),
                wifi_access = 2,
                outlet_accessibility = 2,
                average_rating = 4,
                created_at = "2024-01-01T00:00:00Z"
            ),
            Cafe(
                id = "2",
                name = "Coffee & Code",
                address = Address(
                    street = "71851 Richard Flats",
                    city = "Oakland",
                    state = "CA",
                    zip_code = "57525"
                ),
                location = Location(
                    coordinates = listOf(-122.4787779091634, 37.76115298322381)
                ),
                amenities = listOf("Parking", "Meeting rooms", "Pet friendly"),
                wifi_access = 1,
                outlet_accessibility = 2,
                average_rating = 4,
                created_at = "2024-01-01T00:00:00Z"
            )
        ))
    }

    override suspend fun createUser(user: UserCreate): UserResponse {
        val newUser = UserResponse(
            id = userIdGenerator.getAndIncrement().toString(),
            name = user.name,
            cafes_visited = user.cafes_visited,
            average_rating = user.average_rating,
            created_at = "2024-01-01T00:00:00Z",
            updated_at = "2024-01-01T00:00:00Z"
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
            average_rating = user.average_rating ?: current.average_rating,
            updated_at = "2024-01-01T00:00:00Z"
        )
        users[index] = updated
        return updated
    }

    override suspend fun deleteUser(userId: String): Map<String, String> {
        val removed = users.removeIf { it.id == userId }
        return mapOf("status" to if (removed) "success" else "not_found")
    }

    override suspend fun searchUsers(query: String): List<UserResponse> {
        return users.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }

    override suspend fun login(credentials: UserLogin): LoginResponse {
        val user = users.find { it.name == credentials.name }
        return if (user != null) {
            LoginResponse(
                message = "Login successful",
                user_id = user.id,
                user_name = user.name
            )
        } else {
            throw IllegalArgumentException("Invalid credentials")
        }
    }

    override suspend fun uploadProfilePicture(
        userId: String,
        file: MultipartBody.Part
    ): FileUploadResponse {
        return FileUploadResponse(
            url = "https://fake-api.com/profile-pictures/test-${userId}.jpg",
            message = "Profile picture uploaded successfully"
        )
    }

    override suspend fun getAllCafes(): List<Cafe> {
        return cafes.toList()
    }

    override suspend fun createCafe(cafe: CafeCreate): Cafe {
        val newCafe = Cafe(
            id = cafeIdGenerator.getAndIncrement().toString(), // FIX: Use proper ID generation
            name = cafe.name,
            address = cafe.address,
            location = cafe.location,
            phone = cafe.phone,
            website = cafe.website,
            opening_hours = cafe.opening_hours,
            amenities = cafe.amenities,
            thumbnail_url = cafe.thumbnail_url,
            wifi_access = cafe.wifi_access,
            outlet_accessibility = cafe.outlet_accessibility,
            average_rating = cafe.average_rating,
            created_at = "2024-01-01T00:00:00Z",
            updated_at = "2024-01-01T00:00:00Z"
        )
        cafes.add(newCafe)
        return newCafe
    }

    override suspend fun getCafeById(cafeId: String): Cafe {
        return cafes.find { it.id == cafeId } // FIX: Remove .toString()
            ?: throw IllegalArgumentException("Cafe not found")
    }

    override suspend fun updateCafe(
        cafeId: String,
        cafe: CafeUpdate
    ): Cafe {
        val index = cafes.indexOfFirst { it.id == cafeId }
        if (index == -1) throw IllegalArgumentException("Cafe not found")

        val current = cafes[index]
        val updated = current.copy(
            name = cafe.name ?: current.name,
            address = cafe.address ?: current.address,
            location = cafe.location ?: current.location,
            phone = cafe.phone ?: current.phone,
            website = cafe.website ?: current.website,
            opening_hours = cafe.opening_hours ?: current.opening_hours,
            amenities = cafe.amenities ?: current.amenities,
            thumbnail_url = cafe.thumbnail_url ?: current.thumbnail_url,
            wifi_access = cafe.wifi_access ?: current.wifi_access,
            outlet_accessibility = cafe.outlet_accessibility ?: current.outlet_accessibility,
            average_rating = cafe.average_rating ?: current.average_rating,
            updated_at = "2024-01-01T00:00:00Z"
        )
        cafes[index] = updated
        return updated
    }

    override suspend fun deleteCafe(cafeId: String): Map<String, String> {
        val removed = cafes.removeIf { it.id == cafeId }
        return mapOf("status" to if (removed) "success" else "not_found")
    }

    override suspend fun searchCafes(query: String): List<Cafe> {
        return cafes.filter { cafe ->
            cafe.name?.contains(query, ignoreCase = true) == true ||
                    cafe.address?.city?.contains(query, ignoreCase = true) == true ||
                    cafe.amenities?.any { it.contains(query, ignoreCase = true) } == true
        }
    }

    override suspend fun findNearbyCafes(
        longitude: Double,
        latitude: Double,
        maxDistance: Double
    ): List<Cafe> {
        return cafes.filter { cafe ->
            cafe.location?.coordinates?.let { coords ->
                val cafeLng = coords[0]
                val cafeLat = coords[1]
                val distance = calculateDistance(latitude, longitude, cafeLat, cafeLng)
                distance <= maxDistance
            } ?: false
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        // Simplified distance calculation for testing (Haversine would be more accurate)
        val deltaLat = lat2 - lat1
        val deltaLon = lon2 - lon1
        return kotlin.math.sqrt(deltaLat * deltaLat + deltaLon * deltaLon) * 111000 // Rough conversion to meters
    }

    override suspend fun findCafesByAmenities(amenities: List<String>): List<Cafe> {
        return cafes.filter { cafe ->
            amenities.any { requestedAmenity ->
                cafe.amenities?.any { cafeAmenity ->
                    cafeAmenity.contains(requestedAmenity, ignoreCase = true)
                } == true
            }
        }
    }

    override suspend fun findCafesByRating(minRating: Double): List<Cafe> {
        return cafes.filter { it.average_rating >= minRating.toInt() }
    }

    override suspend fun getCafePhotos(cafeId: String): List<Map<String, Any>> {
        return listOf(
            mapOf(
                "id" to "photo_1",
                "url" to "https://fake-api.com/photos/cafe_${cafeId}_1.jpg",
                "caption" to "Interior view"
            ),
            mapOf(
                "id" to "photo_2",
                "url" to "https://fake-api.com/photos/cafe_${cafeId}_2.jpg",
                "caption" to "Coffee bar"
            )
        )
    }

    override suspend fun createReview(review: ReviewCreate): Review {
        val newReview = Review(
            id = reviewIdGenerator.getAndIncrement().toString(), // FIX: Now reviewIdGenerator is defined
            study_spot_id = review.study_spot_id,
            user_id = review.user_id,
            overall_rating = review.overall_rating,
            outlet_accessibility = review.outlet_accessibility,
            wifi_quality = review.wifi_quality,
            atmosphere = review.atmosphere,
            energy_level = review.energy_level,
            study_friendly = review.study_friendly,
            created_at = "2024-01-01T00:00:00Z",
            updated_at = "2024-01-01T00:00:00Z"
        )
        reviews.add(newReview)
        return newReview
    }

    override suspend fun getReviewById(reviewId: String): Review {
        return reviews.find { it.id == reviewId }
            ?: throw IllegalArgumentException("Review not found")
    }

    override suspend fun updateReview(reviewId: String, review: ReviewUpdate): Review {
        val index = reviews.indexOfFirst { it.id == reviewId }
        if (index == -1) throw IllegalArgumentException("Review not found")

        val current = reviews[index]
        val updated = current.copy(
            overall_rating = review.overall_rating ?: current.overall_rating,
            outlet_accessibility = review.outlet_accessibility ?: current.outlet_accessibility,
            wifi_quality = review.wifi_quality ?: current.wifi_quality,
            atmosphere = review.atmosphere ?: current.atmosphere,
            energy_level = review.energy_level ?: current.energy_level,
            study_friendly = review.study_friendly ?: current.study_friendly,
            updated_at = "2024-01-01T00:00:00Z"
        )
        reviews[index] = updated
        return updated
    }

    override suspend fun deleteReview(reviewId: String): Map<String, String> {
        val removed = reviews.removeIf { it.id == reviewId }
        return mapOf("status" to if (removed) "success" else "not_found")
    }

    override suspend fun getReviewsByStudySpot(studySpotId: String): List<Review> {
        return reviews.filter { it.study_spot_id == studySpotId }
    }

    override suspend fun getReviewsByUser(userId: String): List<Review> {
        return reviews.filter { it.user_id == userId }
    }

    override suspend fun addPhotoToReview(reviewId: String, photo: PhotoCreate): Review {
        val review = getReviewById(reviewId)
        val updatedPhotos = review.photos + Photo(
            id = "photo_${System.currentTimeMillis()}",
            url = photo.url,
            caption = photo.caption
        )
        val updatedReview = review.copy(photos = updatedPhotos)

        val index = reviews.indexOfFirst { it.id == reviewId }
        reviews[index] = updatedReview
        return updatedReview
    }

    override suspend fun uploadPhotoToReview(reviewId: String, file: MultipartBody.Part, caption: String): Review {
        val fakeUrl = "https://fake-api.com/review-photos/review_${reviewId}_${System.currentTimeMillis()}.jpg"
        return addPhotoToReview(reviewId, PhotoCreate(fakeUrl, caption))
    }

    // BOOKMARK ENDPOINTS - FIX THE TODO
    override suspend fun createBookmark(bookmark: BookmarkCreate): Bookmark {
        val newBookmark = Bookmark(
            id = bookmarkIdGenerator.getAndIncrement().toString(),
            user_id = bookmark.user_id,
            cafe_id = bookmark.cafe_id,
            bookmarked_at = "2024-01-01T00:00:00Z"
        )
        bookmarks.add(newBookmark)
        return newBookmark
    }

    override suspend fun getBookmarkById(bookmarkId: String): Bookmark {
        return bookmarks.find { it.id == bookmarkId }
            ?: throw IllegalArgumentException("Bookmark not found")
    }

    override suspend fun deleteBookmarkById(bookmarkId: String): Map<String, String> {
        val removed = bookmarks.removeIf { it.id == bookmarkId }
        return mapOf("status" to if (removed) "success" else "not_found")
    }

    override suspend fun deleteUserBookmark(userId: String, studySpotId: String): Map<String, String> {
        val removed = bookmarks.removeIf { it.user_id == userId && it.cafe_id == studySpotId }
        return mapOf("status" to if (removed) "success" else "not_found")
    }

    override suspend fun getUserBookmarks(userId: String): List<BookmarkWithCafe> {
        return bookmarks.filter { it.user_id == userId }.mapNotNull { bookmark ->
            val cafe = cafes.find { it.id == bookmark.cafe_id }
            cafe?.let {
                BookmarkWithCafe(
                    id = bookmark.id,
                    user_id = bookmark.user_id,
                    cafe_id = bookmark.cafe_id,
                    bookmarked_at = bookmark.bookmarked_at,
                    cafe = CafeInBookmark(
                        id = it.id,
                        name = it.name,
                        address = it.address,
                        location = it.location,
                        phone = it.phone,
                        website = it.website,
                        opening_hours = it.opening_hours,
                        amenities = it.amenities,
                        thumbnail_url = it.thumbnail_url,
                        wifi_access = it.wifi_access,
                        outlet_accessibility = it.outlet_accessibility,
                        average_rating = it.average_rating,
                        created_at = it.created_at,
                        updated_at = it.updated_at
                    )
                )
            }
        }
    }

    override suspend fun deleteUserCafeBookmark(userId: String, cafeId: String): Map<String, String> {
        val removed = bookmarks.removeIf { it.user_id == userId && it.cafe_id == cafeId }
        return mapOf("status" to if (removed) "success" else "not_found")
    }

    override suspend fun checkBookmarkExists(userId: String, cafeId: String): Map<String, Boolean> {
        val exists = bookmarks.any { it.user_id == userId && it.cafe_id == cafeId }
        return mapOf("exists" to exists)
    }

    // File management endpoints
    override suspend fun uploadFile(file: MultipartBody.Part): FileUploadResponse {
        return FileUploadResponse(
            url = "https://fake-api.com/files/uploaded_${System.currentTimeMillis()}.jpg",
            message = "File uploaded successfully"
        )
    }

    override suspend fun uploadMultipleFiles(files: List<MultipartBody.Part>): Map<String, Any> {
        val urls = files.mapIndexed { index, _ ->
            "https://fake-api.com/files/batch_${System.currentTimeMillis()}_${index}.jpg"
        }
        return mapOf(
            "message" to "Files uploaded successfully",
            "urls" to urls,
            "count" to files.size
        )
    }

    override suspend fun deleteFile(fileUrl: String): Map<String, String> {
        return mapOf(
            "status" to "success",
            "message" to "File deleted successfully"
        )
    }

    override suspend fun getFileInfo(fileUrl: String): Map<String, Any> {
        return mapOf(
            "url" to fileUrl,
            "size" to 1024000,
            "type" to "image/jpeg",
            "created_at" to "2024-01-01T00:00:00Z"
        )
    }

    // Health check endpoints
    override suspend fun apiRoot(): Map<String, Any> {
        return mapOf(
            "message" to "Fake API Service",
            "version" to "1.0.0",
            "status" to "running"
        )
    }

    override suspend fun healthCheck(): Map<String, Any> {
        return mapOf(
            "status" to "healthy",
            "timestamp" to System.currentTimeMillis(),
            "uptime" to "100%"
        )
    }

    // Helper methods for testing
    fun clearAllData() {
        users.clear()
        cafes.clear()
        reviews.clear()
        bookmarks.clear()
    }

    fun getCafesCount(): Int = cafes.size
    fun getUsersCount(): Int = users.size
    fun getReviewsCount(): Int = reviews.size
    fun getBookmarksCount(): Int = bookmarks.size
}