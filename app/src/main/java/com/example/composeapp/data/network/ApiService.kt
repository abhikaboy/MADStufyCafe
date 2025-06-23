package com.example.composeapp.data.network

import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiService {
    
    // User endpoints
    @POST("api/v1/users/")
    suspend fun createUser(@Body user: UserCreate): UserResponse
    
    @GET("api/v1/users/")
    suspend fun getAllUsers(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100
    ): List<UserResponse>
    
    @GET("api/v1/users/{user_id}")
    suspend fun getUserById(@Path("user_id") userId: String): UserResponse
    
    @PUT("api/v1/users/{user_id}")
    suspend fun updateUser(@Path("user_id") userId: String, @Body user: UserUpdate): UserResponse
    
    @DELETE("api/v1/users/{user_id}")
    suspend fun deleteUser(@Path("user_id") userId: String): Map<String, String>
    
    @GET("api/v1/users/search/")
    suspend fun searchUsers(@Query("query") query: String): List<UserResponse>
    
    @POST("api/v1/login")
    suspend fun login(@Body credentials: UserLogin): LoginResponse
    
    @Multipart
    @POST("api/v1/users/{user_id}/profile-picture")
    suspend fun uploadProfilePicture(
        @Path("user_id") userId: String,
        @Part file: MultipartBody.Part
    ): FileUploadResponse
    
    // Cafe endpoints
    @GET("api/v1/cafes/")
    suspend fun getAllCafes(): List<Cafe>
    
    @POST("api/v1/cafes/")
    suspend fun createCafe(@Body cafe: CafeCreate): Cafe
    
    @GET("api/v1/cafes/{cafe_id}")
    suspend fun getCafeById(@Path("cafe_id") cafeId: String): Cafe
    
    @PUT("api/v1/cafes/{cafe_id}")
    suspend fun updateCafe(@Path("cafe_id") cafeId: String, @Body cafe: CafeUpdate): Cafe
    
    @DELETE("api/v1/cafes/{cafe_id}")
    suspend fun deleteCafe(@Path("cafe_id") cafeId: String): Map<String, String>
    
    @GET("api/v1/cafes/search/")
    suspend fun searchCafes(@Query("query") query: String): List<Cafe>
    
    @GET("api/v1/cafes/nearby/")
    suspend fun findNearbyCafes(
        @Query("longitude") longitude: Double,
        @Query("latitude") latitude: Double,
        @Query("max_distance") maxDistance: Double = 5000.0
    ): List<Cafe>
    
    @GET("api/v1/cafes/by-amenities/")
    suspend fun findCafesByAmenities(@Query("amenities") amenities: List<String>): List<Cafe>
    
    @GET("api/v1/cafes/by-rating/")
    suspend fun findCafesByRating(@Query("min_rating") minRating: Double): List<Cafe>
    
    @GET("api/v1/cafes/{cafe_id}/photos")
    suspend fun getCafePhotos(@Path("cafe_id") cafeId: String): List<Map<String, Any>>
    
    // Review endpoints
    @POST("api/v1/reviews/")
    suspend fun createReview(@Body review: ReviewCreate): Review
    
    @GET("api/v1/reviews/{review_id}")
    suspend fun getReviewById(@Path("review_id") reviewId: String): Review
    
    @PUT("api/v1/reviews/{review_id}")
    suspend fun updateReview(@Path("review_id") reviewId: String, @Body review: ReviewUpdate): Review
    
    @DELETE("api/v1/reviews/{review_id}")
    suspend fun deleteReview(@Path("review_id") reviewId: String): Map<String, String>
    
    @GET("api/v1/reviews/by-spot/{study_spot_id}")
    suspend fun getReviewsByStudySpot(@Path("study_spot_id") studySpotId: String): List<Review>
    
    @GET("api/v1/reviews/by-user/{user_id}")
    suspend fun getReviewsByUser(@Path("user_id") userId: String): List<Review>
    
    @POST("api/v1/reviews/{review_id}/photos")
    suspend fun addPhotoToReview(@Path("review_id") reviewId: String, @Body photo: PhotoCreate): Review
    
    @Multipart
    @POST("api/v1/reviews/{review_id}/upload-photo")
    suspend fun uploadPhotoToReview(
        @Path("review_id") reviewId: String,
        @Part file: MultipartBody.Part,
        @Query("caption") caption: String = ""
    ): Review
    
    // Bookmark endpoints
    @POST("api/v1/bookmarks/")
    suspend fun createBookmark(@Body bookmark: BookmarkCreate): Bookmark
    
    @GET("api/v1/bookmarks/{bookmark_id}")
    suspend fun getBookmarkById(@Path("bookmark_id") bookmarkId: String): Bookmark
    
    @DELETE("api/v1/bookmarks/{bookmark_id}")
    suspend fun deleteBookmarkById(@Path("bookmark_id") bookmarkId: String): Map<String, String>
    
    @DELETE("api/v1/users/{user_id}/bookmarks/{study_spot_id}")
    suspend fun deleteUserBookmark(@Path("user_id") userId: String, @Path("study_spot_id") studySpotId: String): Map<String, String>
    
    @GET("api/v1/users/{user_id}/bookmarks")
    suspend fun getUserBookmarks(@Path("user_id") userId: String): List<BookmarkWithCafe>
    
    @DELETE("api/v1/users/{user_id}/bookmarks/{cafe_id}")
    suspend fun deleteUserCafeBookmark(
        @Path("user_id") userId: String,
        @Path("cafe_id") cafeId: String
    ): Map<String, String>
    
    @GET("api/v1/users/{user_id}/bookmarks/{cafe_id}/exists")
    suspend fun checkBookmarkExists(
        @Path("user_id") userId: String,
        @Path("cafe_id") cafeId: String
    ): Map<String, Boolean>
    
    // File management endpoints
    @Multipart
    @POST("api/v1/files/upload")
    suspend fun uploadFile(@Part file: MultipartBody.Part): FileUploadResponse
    
    @Multipart
    @POST("api/v1/files/upload-multiple")
    suspend fun uploadMultipleFiles(@Part files: List<MultipartBody.Part>): Map<String, Any>
    
    @DELETE("api/v1/files/delete")
    suspend fun deleteFile(@Query("file_url") fileUrl: String): Map<String, String>
    
    @GET("api/v1/files/info")
    suspend fun getFileInfo(@Query("file_url") fileUrl: String): Map<String, Any>
    
    // Health check endpoints
    @GET("/")
    suspend fun apiRoot(): Map<String, Any>
    
    @GET("/health")
    suspend fun healthCheck(): Map<String, Any>
}

// Data classes for API requests that weren't included in the models
data class UserUpdate(
    val name: String? = null,
    val cafes_visited: Int? = null,
    val average_rating: Double? = null,
    val password: String? = null
)

data class CafeUpdate(
    val name: String? = null,
    val address: Address? = null,
    val location: Location? = null,
    val phone: String? = null,
    val website: String? = null,
    val opening_hours: Map<String, String>? = null,
    val amenities: List<String>? = null,
    val thumbnail_url: String? = null,
    val wifi_access: Int? = null,
    val outlet_accessibility: Int? = null,
    val average_rating: Int? = null
)

data class ReviewUpdate(
    val overall_rating: Double? = null,
    val outlet_accessibility: Double? = null,
    val wifi_quality: Double? = null,
    val atmosphere: String? = null,
    val energy_level: String? = null,
    val study_friendly: String? = null
)

data class PhotoCreate(
    val url: String,
    val caption: String? = null
) 