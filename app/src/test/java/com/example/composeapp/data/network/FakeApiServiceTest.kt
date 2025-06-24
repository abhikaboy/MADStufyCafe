package com.example.composeapp.data.network

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.composeapp.data.network.fakes.FakeApiService
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.*

class FakeApiServiceTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var underTest: FakeApiService

    @Before
    fun setUp() {
        underTest = FakeApiService()
    }


    @Test
    fun whenCreateUserCalledThenUserIsCreatedSuccessfully() = runTest {
        // Arrange
        val userCreate = UserCreate(
            name = "John Doe",
            password = "password123",
            cafes_visited = 2,
            average_rating = 4.5
        )

        // Act
        val result = underTest.createUser(userCreate)

        // Assert
        assertEquals("John Doe", result.name)
        assertEquals(2, result.cafes_visited)
        assertEquals(4.5, result.average_rating, 0.01)
        assertNotNull(result.id)
        assertNotNull(result.created_at)
    }

    @Test
    fun whenGetAllUsersCalledThenReturnsAllUsers() = runTest {
        // Arrange
        val initialCount = underTest.getAllUsers().size
        // Act
        val result = underTest.getAllUsers()
        // Assert
        assertEquals(initialCount, result.size)
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun whenGetUserByValidIdThenReturnsCorrectUser() = runTest {
        // Arrange
        val newUser = underTest.createUser(UserCreate("Test User", "password"))
        val userId = newUser.id
        // Act
        val result = underTest.getUserById(userId)
        // Assert
        assertEquals(userId, result.id)
        assertEquals("Test User", result.name)
    }

    @Test
    fun whenGetUserByInvalidIdThenThrowsException() = runTest {
        // Arrange
        val invalidId = "999999"
        // Act & Assert
        try {
            underTest.getUserById(invalidId)
            fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("User not found", e.message)
        }
    }

    @Test
    fun whenUpdateUserCalledThenUserIsUpdatedCorrectly() = runTest {
        // Arrange
        val user = underTest.createUser(UserCreate("Original Name", "password"))
        val update = UserUpdate(
            name = "Updated Name",
            cafes_visited = 10,
            average_rating = 4.8
        )
        // Act
        val result = underTest.updateUser(user.id, update)
        // Assert
        assertEquals("Updated Name", result.name)
        assertEquals(10, result.cafes_visited)
        assertEquals(4.8, result.average_rating, 0.01)
        assertEquals(user.id, result.id)
    }

    @Test
    fun whenDeleteUserCalledThenUserIsDeletedSuccessfully() = runTest {
        // Arrange
        val user = underTest.createUser(UserCreate("User To Delete", "password"))
        val userId = user.id
        // Act
        val result = underTest.deleteUser(userId)
        // Assert
        assertEquals("success", result["status"])
        try {
            underTest.getUserById(userId)
            fail("User should have been deleted")
        } catch (e: IllegalArgumentException) {
            assertEquals("User not found", e.message)
        }
    }

    @Test
    fun whenSearchUsersCalledThenReturnsMatchingUsers() = runTest {
        // Arrange
        underTest.createUser(UserCreate("John Coffee", "password"))
        underTest.createUser(UserCreate("Jane Tea", "password"))
        underTest.createUser(UserCreate("Bob Coffee", "password"))
        // Act
        val results = underTest.searchUsers("Coffee")
        // Assert
        assertEquals(2, results.size)
        assertTrue(results.all { it.name.contains("Coffee") })
    }

    @Test
    fun whenLoginWithValidCredentialsThenReturnsSuccess() = runTest {
        // Arrange
        val user = underTest.createUser(UserCreate("LoginUser", "password"))
        val credentials = UserLogin("LoginUser", "password")
        // Act
        val result = underTest.login(credentials)
        // Assert
        assertEquals("Login successful", result.message)
        assertEquals(user.id, result.user_id)
        assertEquals("LoginUser", result.user_name)
    }

    @Test
    fun whenLoginWithInvalidCredentialsThenThrowsException() = runTest {
        // Arrange
        val credentials = UserLogin("NonExistentUser", "password")
        // Act & Assert
        try {
            underTest.login(credentials)
            fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("Invalid credentials", e.message)
        }
    }


    @Test
    fun whenGetAllCafesCalledThenReturnsAllCafes() = runTest {
        // Arrange
        val initialCount = underTest.getAllCafes().size
        // Act
        val result = underTest.getAllCafes()
        // Assert
        assertEquals(initialCount, result.size)
        assertTrue(result.isNotEmpty()) // Should have pre-populated test data
    }

    @Test
    fun whenCreateCafeCalledThenCafeIsCreatedSuccessfully() = runTest {
        // Arrange
        val cafeCreate = CafeCreate(
            name = "New Test Cafe",
            address = Address("123 Test St", "Test City", "CA", "12345"),
            location = Location(coordinates = listOf(-122.0, 37.0)),
            phone = "555-1234",
            website = "https://test.com",
            opening_hours = mapOf("Monday" to "9AM-5PM", "Tuesday" to "9AM-5PM"),
            amenities = listOf("WiFi", "Parking"),
            thumbnail_url = "https://test.com/image.jpg",
            wifi_access = 3,
            outlet_accessibility = 2,
            average_rating = 4
        )
        // Act
        val result = underTest.createCafe(cafeCreate)
        // Assert
        assertEquals("New Test Cafe", result.name)
        assertEquals(3, result.wifi_access)
        assertEquals(2, result.outlet_accessibility)
        assertEquals(4, result.average_rating)
        assertNotNull(result.id)
    }

    @Test
    fun whenGetCafeByValidIdThenReturnsCorrectCafe() = runTest {
        // Arrange
        val cafe = underTest.createCafe(CafeCreate(
            name = "Test Cafe",
            address = null,
            location = null,
            phone = null,
            website = null,
            opening_hours = null,
            amenities = null,
            thumbnail_url = null,
            wifi_access = 0,
            outlet_accessibility = 0,
            average_rating = 1
        ))
        val cafeId = cafe.id
        // Act
        val result = underTest.getCafeById(cafeId)
        // Assert
        assertEquals(cafeId, result.id)
        assertEquals("Test Cafe", result.name)
    }

    @Test
    fun whenGetCafeByInvalidIdThenThrowsException() = runTest {
        // Arrange
        val invalidId = "999999"
        // Act & Assert
        try {
            underTest.getCafeById(invalidId)
            fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("Cafe not found", e.message)
        }
    }

    @Test
    fun whenSearchCafesCalledThenReturnsMatchingCafes() = runTest {
        // Arrange
        underTest.createCafe(CafeCreate(
            name = "Coffee Paradise",
            address = null,
            location = null,
            phone = null,
            website = null,
            opening_hours = null,
            amenities = null,
            thumbnail_url = null,
            wifi_access = 0,
            outlet_accessibility = 0,
            average_rating = 1
        ))
        underTest.createCafe(CafeCreate(
            name = "Tea House",
            address = null,
            location = null,
            phone = null,
            website = null,
            opening_hours = null,
            amenities = null,
            thumbnail_url = null,
            wifi_access = 0,
            outlet_accessibility = 0,
            average_rating = 1
        ))
        underTest.createCafe(CafeCreate(
            name = "Coffee Central",
            address = null,
            location = null,
            phone = null,
            website = null,
            opening_hours = null,
            amenities = null,
            thumbnail_url = null,
            wifi_access = 0,
            outlet_accessibility = 0,
            average_rating = 1
        ))

        // Act
        val results = underTest.searchCafes("Coffee")

        // Assert
        assertTrue(results.size >= 2) // At least our 2 test cafes
        assertTrue(results.any { it.name?.contains("Coffee") == true })
    }

    @Test
    fun whenSearchCafesWithEmptyQueryThenReturnsAllCafes() = runTest {
        // Arrange
        val totalCafes = underTest.getAllCafes().size
        // Act
        val results = underTest.searchCafes("")
        // Assert
        assertEquals(totalCafes, results.size)
    }

    @Test
    fun whenFindCafesByAmenitiesCalledThenReturnsMatchingCafes() = runTest {
        // Arrange
        underTest.createCafe(CafeCreate(
            name = "WiFi Cafe",
            address = null,
            location = null,
            phone = null,
            website = null,
            opening_hours = null,
            amenities = listOf("Free WiFi", "Power outlets"),
            thumbnail_url = null,
            wifi_access = 0,
            outlet_accessibility = 0,
            average_rating = 1
        ))
        underTest.createCafe(CafeCreate(
            name = "Parking Cafe",
            address = null,
            location = null,
            phone = null,
            website = null,
            opening_hours = null,
            amenities = listOf("Parking", "Outdoor seating"),
            thumbnail_url = null,
            wifi_access = 0,
            outlet_accessibility = 0,
            average_rating = 1
        ))

        // Act
        val results = underTest.findCafesByAmenities(listOf("WiFi"))

        // Assert
        assertTrue(results.isNotEmpty())
        assertTrue(results.any { cafe ->
            cafe.amenities?.any { it.contains("WiFi", ignoreCase = true) } == true
        })
    }

    @Test
    fun whenFindCafesByRatingCalledThenReturnsHighRatedCafes() = runTest {
        // Arrange
        underTest.createCafe(CafeCreate(
            name = "Low Rating",
            address = null,
            location = null,
            phone = null,
            website = null,
            opening_hours = null,
            amenities = null,
            thumbnail_url = null,
            wifi_access = 0,
            outlet_accessibility = 0,
            average_rating = 2
        ))
        underTest.createCafe(CafeCreate(
            name = "High Rating",
            address = null,
            location = null,
            phone = null,
            website = null,
            opening_hours = null,
            amenities = null,
            thumbnail_url = null,
            wifi_access = 0,
            outlet_accessibility = 0,
            average_rating = 5
        ))

        // Act
        val results = underTest.findCafesByRating(4.0)

        // Assert
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.average_rating >= 4 })
    }

    @Test
    fun whenFindNearbyCafesCalledThenReturnsNearbyLocations() = runTest {
        // Arrange
        underTest.createCafe(CafeCreate(
            name = "Close Cafe",
            address = null,
            location = Location(coordinates = listOf(-122.0, 37.0)),
            phone = null,
            website = null,
            opening_hours = null,
            amenities = null,
            thumbnail_url = null,
            wifi_access = 0,
            outlet_accessibility = 0,
            average_rating = 1
        ))
        underTest.createCafe(CafeCreate(
            name = "Far Cafe",
            address = null,
            location = Location(coordinates = listOf(-74.0, 40.0)),
            phone = null,
            website = null,
            opening_hours = null,
            amenities = null,
            thumbnail_url = null,
            wifi_access = 0,
            outlet_accessibility = 0,
            average_rating = 1
        ))

        // Act
        val results = underTest.findNearbyCafes(-122.0, 37.0, 1000.0)
        // Assert
        assertTrue(results.isNotEmpty())
        assertTrue(results.any { it.name == "Close Cafe" })
    }


    @Test
    fun whenCreateReviewCalledThenReviewIsCreatedSuccessfully() = runTest {
        // Arrange
        val reviewCreate = ReviewCreate(
            study_spot_id = "1",
            user_id = "1",
            overall_rating = 4.5,
            outlet_accessibility = 4.0,
            wifi_quality = 3.5,
            atmosphere = listOf("Cozy"),
            energy_level = listOf("Moderate"),
            study_friendly = listOf("Very friendly")
        )
        // Act
        val result = underTest.createReview(reviewCreate)
        // Assert
        assertEquals("1", result.study_spot_id)
        assertEquals("1", result.user_id)
        assertEquals(4.5, result.overall_rating, 0.01)
        assertEquals("Cozy", result.atmosphere)
        assertNotNull(result.id)
    }

    @Test
    fun whenGetReviewsByStudySpotCalledThenReturnsMatchingReviews() = runTest {
        // Arrange
        val studySpotId = "cafe123"
        underTest.createReview(ReviewCreate(
            study_spot_id = studySpotId,
            user_id = "1",
            overall_rating = 4.0,
            outlet_accessibility = 3.0,
            wifi_quality = 4.0
        ))
        underTest.createReview(ReviewCreate(
            study_spot_id = studySpotId,
            user_id = "2",
            overall_rating = 5.0,
            outlet_accessibility = 4.0,
            wifi_quality = 5.0
        ))
        underTest.createReview(ReviewCreate(
            study_spot_id = "other",
            user_id = "3",
            overall_rating = 3.0,
            outlet_accessibility = 2.0,
            wifi_quality = 3.0
        ))

        // Act
        val results = underTest.getReviewsByStudySpot(studySpotId)
        // Assert
        assertEquals(2, results.size)
        assertTrue(results.all { it.study_spot_id == studySpotId })
    }

    @Test
    fun whenGetReviewsByUserCalledThenReturnsMatchingReviews() = runTest {
        // Arrange
        val userId = "user123"
        underTest.createReview(ReviewCreate(
            study_spot_id = "cafe1",
            user_id = userId,
            overall_rating = 4.0,
            outlet_accessibility = 3.0,
            wifi_quality = 4.0
        ))
        underTest.createReview(ReviewCreate(
            study_spot_id = "cafe2",
            user_id = userId,
            overall_rating = 5.0,
            outlet_accessibility = 4.0,
            wifi_quality = 5.0
        ))
        underTest.createReview(ReviewCreate(
            study_spot_id = "cafe3",
            user_id = "other_user",
            overall_rating = 3.0,
            outlet_accessibility = 2.0,
            wifi_quality = 3.0
        ))
        // Act
        val results = underTest.getReviewsByUser(userId)
        // Assert
        assertEquals(2, results.size)
        assertTrue(results.all { it.user_id == userId })
    }

    @Test
    fun whenUpdateReviewCalledThenReviewIsUpdatedCorrectly() = runTest {
        // Arrange
        val review = underTest.createReview(ReviewCreate(
            study_spot_id = "cafe1",
            user_id = "user1",
            overall_rating = 3.0,
            outlet_accessibility = 2.0,
            wifi_quality = 3.0,
            atmosphere = listOf("Original")
        ))
        val update = ReviewUpdate(
            overall_rating = 5.0,
            atmosphere = listOf("Updated Atmosphere")
        )
        // Act
        val result = underTest.updateReview(review.id, update)
        // Assert
        assertEquals(5.0, result.overall_rating, 0.01)
        assertEquals(listOf("Updated Atmosphere"), result.atmosphere)
        assertEquals(review.id, result.id)
        assertEquals(2.0, result.outlet_accessibility, 0.01) // Should remain unchanged
    }

    @Test
    fun whenCreateBookmarkCalledThenBookmarkIsCreatedSuccessfully() = runTest {
        // Arrange
        val bookmarkCreate = BookmarkCreate(
            user_id = "user123",
            cafe_id = "cafe456"
        )
        // Act
        val result = underTest.createBookmark(bookmarkCreate)
        // Assert
        assertEquals("user123", result.user_id)
        assertEquals("cafe456", result.cafe_id)
        assertNotNull(result.id)
        assertNotNull(result.bookmarked_at)
    }

    @Test
    fun whenCheckBookmarkExistsCalledThenReturnsCorrectStatus() = runTest {
        // Arrange
        val userId = "user123"
        val cafeId = "cafe456"
        underTest.createBookmark(BookmarkCreate(userId, cafeId))
        // Act
        val existsResult = underTest.checkBookmarkExists(userId, cafeId)
        val notExistsResult = underTest.checkBookmarkExists(userId, "nonexistent")
        // Assert
        assertEquals(true, existsResult["exists"])
        assertEquals(false, notExistsResult["exists"])
    }

    @Test
    fun whenDeleteUserCafeBookmarkCalledThenBookmarkIsDeleted() = runTest {
        // Arrange
        val userId = "user123"
        val cafeId = "cafe456"
        underTest.createBookmark(BookmarkCreate(userId, cafeId))
        // Act
        val result = underTest.deleteUserCafeBookmark(userId, cafeId)
        // Assert
        assertEquals("success", result["status"])
        val checkResult = underTest.checkBookmarkExists(userId, cafeId)
        assertEquals(false, checkResult["exists"])
    }

    @Test
    fun whenGetUserBookmarksCalledThenReturnsBookmarksWithCafeDetails() = runTest {
        // Arrange
        val userId = "user123"
        val cafe = underTest.createCafe(CafeCreate(
            name = "Bookmarked Cafe",
            address = null,
            location = null,
            phone = null,
            website = null,
            opening_hours = null,
            amenities = null,
            thumbnail_url = null,
            wifi_access = 0,
            outlet_accessibility = 0,
            average_rating = 1
        ))
        underTest.createBookmark(BookmarkCreate(userId, cafe.id))

        // Act
        val results = underTest.getUserBookmarks(userId)

        // Assert
        assertEquals(1, results.size)
        assertEquals(userId, results[0].user_id)
        assertEquals(cafe.id, results[0].cafe_id)
        assertEquals("Bookmarked Cafe", results[0].cafe?.name)
    }

    @Test
    fun whenSearchUsersWithNonExistentQueryThenReturnsEmptyList() = runTest {
        // Arrange
        val query = "NonExistentUserName12345"
        // Act
        val results = underTest.searchUsers(query)

        // Assert
        assertTrue(results.isEmpty())
    }

    @Test
    fun whenUpdateNonExistentUserThenThrowsException() = runTest {
        // Arrange
        val invalidId = "999999"
        val update = UserUpdate(name = "Updated Name")
        // Act & Assert
        try {
            underTest.updateUser(invalidId, update)
            fail("Expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("User not found", e.message)
        }
    }

    @Test
    fun whenDeleteNonExistentUserThenReturnsNotFound() = runTest {
        // Arrange
        val invalidId = "999999"

        // Act
        val result = underTest.deleteUser(invalidId)
        // Assert
        assertEquals("not_found", result["status"])
    }

    @Test
    fun whenClearAllDataCalledThenAllDataIsCleared() = runTest {
        // Arrange
        underTest.createUser(UserCreate("Test User", "password"))
        underTest.createCafe(CafeCreate(
            name = "Test Cafe",
            address = null,
            location = null,
            phone = null,
            website = null,
            opening_hours = null,
            amenities = null,
            thumbnail_url = null,
            wifi_access = 0,
            outlet_accessibility = 0,
            average_rating = 1
        ))

        // Act
        underTest.clearAllData()
        // Assert
        assertEquals(0, underTest.getUsersCount())
        assertEquals(0, underTest.getCafesCount())
        assertEquals(0, underTest.getReviewsCount())
        assertEquals(0, underTest.getBookmarksCount())
    }


    @Test
    fun whenHealthCheckCalledThenReturnsHealthyStatus() = runTest {
        // Act
        val result = underTest.healthCheck()

        // Assert
        assertEquals("healthy", result["status"])
        assertNotNull(result["timestamp"])
        assertEquals("100%", result["uptime"])
    }

    @Test
    fun whenApiRootCalledThenReturnsApiInfo() = runTest {
        // Act
        val result = underTest.apiRoot()

        // Assert
        assertEquals("Fake API Service", result["message"])
        assertEquals("1.0.0", result["version"])
        assertEquals("running", result["status"])
    }

    @Test
    fun whenUploadProfilePictureCalledThenReturnsSuccessResponse() = runTest {
        // Arrange
        val userId = "123"
        assertNotNull(underTest::uploadProfilePicture)
    }

    @Test
    fun whenGetCafePhotosCalledThenReturnsPhotos() = runTest {
        // Arrange
        val cafeId = "123"

        // Act
        val result = underTest.getCafePhotos(cafeId)

        // Assert
        assertEquals(2, result.size)
        assertTrue(result[0]["url"].toString().contains(cafeId))
        assertTrue(result[1]["url"].toString().contains(cafeId))
    }
}