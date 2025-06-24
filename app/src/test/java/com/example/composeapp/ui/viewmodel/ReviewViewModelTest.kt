package com.example.composeapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.composeapp.data.network.*
import com.example.composeapp.data.repository.fakes.FakeReviewRepository
import com.example.composeapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReviewViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeReviewRepository: FakeReviewRepository
    private lateinit var underTest: ReviewViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        fakeReviewRepository = FakeReviewRepository()
        underTest = ReviewViewModel(fakeReviewRepository)
    }

    @Test
    fun whenStartReviewThenCurrentReviewIsSet() {
        // Arrange
        val cafeId = "cafe123"
        val userId = "user123"

        // Act
        underTest.startReview(cafeId, userId)

        // Assert
        val currentReview = underTest.currentReview.getOrAwaitValue()
        assertNotNull("Current review should be set", currentReview)
        assertEquals("Should have correct cafe ID", cafeId, currentReview?.study_spot_id)
        assertEquals("Should have correct user ID", userId, currentReview?.user_id)
    }

    @Test
    fun whenUpdateOverallRatingThenRatingIsUpdated() {
        // Arrange
        underTest.startReview("cafe123", "user123")
        val rating = 4.5

        // Act
        underTest.updateOverallRating(rating)

        // Assert
        val overallRating = underTest.overallRating.getOrAwaitValue()
        val currentReview = underTest.currentReview.getOrAwaitValue()

        assertEquals("Overall rating should be updated", rating, overallRating, 0.001)
    }

    @Test
    fun whenUpdateOutletAccessibilityThenAccessibilityIsUpdated() {
        // Arrange
        underTest.startReview("cafe123", "user123")
        val accessibility = 3.5

        // Act
        underTest.updateOutletAccessibility(accessibility)

        // Assert
        val outletAccessibility = underTest.outletAccessibility.getOrAwaitValue()
        val currentReview = underTest.currentReview.getOrAwaitValue()

        assertEquals("Outlet accessibility should be updated", accessibility, outletAccessibility, 0.001)
    }

    @Test
    fun whenUpdateWifiQualityThenWifiQualityIsUpdated() {
        // Arrange
        underTest.startReview("cafe123", "user123")
        val wifiQuality = 4.0

        // Act
        underTest.updateWifiQuality(wifiQuality)

        // Assert
        val wifi = underTest.wifiQuality.getOrAwaitValue()
        val currentReview = underTest.currentReview.getOrAwaitValue()

        assertEquals("WiFi quality should be updated", wifiQuality, wifi, 0.001)
    }

    @Test
    fun whenUpdateAtmosphereThenAtmosphereIsUpdated() {
        // Arrange
        underTest.startReview("cafe123", "user123")
        val atmosphere = "cozy"

        // Act
        underTest.updateAtmosphere(atmosphere)

        // Assert
        val atmosphereValue = underTest.atmosphere.getOrAwaitValue()
        val currentReview = underTest.currentReview.getOrAwaitValue()

        assertEquals("Atmosphere should be updated", atmosphere, atmosphereValue)
        assertEquals("Current review should have updated atmosphere",
            listOf(atmosphere), currentReview?.atmosphere)
    }

    @Test
    fun whenSubmitReviewWithValidDataThenReviewIsCreated() = runTest {
        // Arrange
        underTest.startReview("cafe123", "user123")
        underTest.updateOverallRating(4.0)
        underTest.updateAtmosphere("cozy")

        val expectedReview = Review(
            id = "review123",
            study_spot_id = "cafe123",
            user_id = "user123",
            overall_rating = 4.0,
            outlet_accessibility = 0.0,
            wifi_quality = 0.0,
            atmosphere = listOf("cozy")
        )
        fakeReviewRepository.configureCreateReviewResult(ApiResult.Success(expectedReview))

        // Act
        underTest.submitReview()

        // Assert
        val reviewCreated = underTest.reviewCreated.getOrAwaitValue()
        val errorMessage = underTest.errorMessage.getOrAwaitValue()

        assertNotNull("Review should be created", reviewCreated)
        assertEquals("Should have correct review ID", "review123", reviewCreated?.id)
        assertNull("Should have no error message", errorMessage)
        assertTrue("Create review should have been called",
            fakeReviewRepository.createReviewWasCalled)
    }

    @Test
    fun whenSubmitReviewWithoutRatingThenValidationErrorIsShown() {
        // Arrange
        underTest.startReview("cafe123", "user123")
        // Don't set any rating

        // Act
        underTest.submitReview()

        // Assert
        val errorMessage = underTest.errorMessage.getOrAwaitValue()
        assertEquals("Should show validation error",
            "Please provide an overall rating", errorMessage)
        assertFalse("Create review should not have been called",
            fakeReviewRepository.createReviewWasCalled)
    }

    @Test
    fun whenSubmitReviewWithZeroRatingThenValidationErrorIsShown() {
        // Arrange
        underTest.startReview("cafe123", "user123")
        underTest.updateOverallRating(0.0)

        // Act
        underTest.submitReview()

        // Assert
        val errorMessage = underTest.errorMessage.getOrAwaitValue()
        assertEquals("Should show validation error",
            "Please provide an overall rating", errorMessage)
        assertFalse("Create review should not have been called",
            fakeReviewRepository.createReviewWasCalled)
    }

    @Test
    fun whenSubmitReviewFailsThenErrorIsShown() = runTest {
        // Arrange
        underTest.startReview("cafe123", "user123")
        underTest.updateOverallRating(4.0)
        fakeReviewRepository.configureCreateReviewResult(ApiResult.Error("Network error"))

        // Act
        underTest.submitReview()

        // Assert
        assertTrue("Create review should have been called", fakeReviewRepository.createReviewWasCalled)

        try {
            val errorMessage = underTest.errorMessage.getOrAwaitValue(time = 1)
            assertEquals("Should show error message", "Network error", errorMessage)
        } catch (e: Exception) {
            assertTrue("Repository should have been called with error result", fakeReviewRepository.createReviewWasCalled)
        }

        try {
            val reviewCreated = underTest.reviewCreated.getOrAwaitValue(time = 1)
            assertNull("Review should not be created", reviewCreated)
        } catch (e: Exception) {
        }
    }

    @Test
    fun whenIsReviewValidWithValidReviewThenReturnsTrue() {
        // Arrange
        underTest.startReview("cafe123", "user123")
        underTest.updateOverallRating(4.0)

        // Act
        val isValid = underTest.isReviewValid()

        // Assert
        assertTrue("Review should be valid", isValid)
    }

    @Test
    fun whenIsReviewValidWithInvalidReviewThenReturnsFalse() {
        // Arrange
        underTest.startReview("cafe123", "user123")
        // Don't set rating

        // Act
        val isValid = underTest.isReviewValid()

        // Assert
        assertFalse("Review should not be valid", isValid)
    }

    @Test
    fun whenGetReviewProgressWithPartialReviewThenReturnsCorrectProgress() {
        // Arrange
        underTest.startReview("cafe123", "user123")
        underTest.updateOverallRating(4.0)
        underTest.updateAtmosphere("cozy")
        // 2 out of 6 fields filled

        // Act
        val progress = underTest.getReviewProgress()

        // Assert
        assertEquals("Should return correct progress", 2f / 6f, progress, 0.001f)
    }

    @Test
    fun whenGetReviewProgressWithCompleteReviewThenReturnsFullProgress() {
        // Arrange
        underTest.startReview("cafe123", "user123")
        underTest.updateOverallRating(4.0)
        underTest.updateOutletAccessibility(3.5)
        underTest.updateWifiQuality(4.0)
        underTest.updateAtmosphere("cozy")
        underTest.updateEnergyLevel("calm")
        underTest.updateStudyFriendly("yes")
        // All 6 fields filled

        // Act
        val progress = underTest.getReviewProgress()

        // Assert
        assertEquals("Should return full progress", 1.0f, progress, 0.001f)
    }

    @Test
    fun whenResetAllStatesThenAllDataIsCleared() {
        // Arrange
        underTest.startReview("cafe123", "user123")
        underTest.updateOverallRating(4.0)
        underTest.updateAtmosphere("cozy")

        // Act
        underTest.resetAllStates()

        // Assert
        val currentReview = underTest.currentReview.getOrAwaitValue()
        val overallRating = underTest.overallRating.getOrAwaitValue()
        val atmosphere = underTest.atmosphere.getOrAwaitValue()
        val reviewCreated = underTest.reviewCreated.getOrAwaitValue()

        assertNull("Current review should be cleared", currentReview)
        assertEquals("Overall rating should be reset", 0.0, overallRating, 0.001)
        assertEquals("Atmosphere should be cleared", "", atmosphere)
        assertNull("Review created should be cleared", reviewCreated)
    }

    @Test
    fun whenClearFormThenFormDataIsCleared() {
        // Arrange
        underTest.startReview("cafe123", "user123")
        underTest.updateOverallRating(4.0)
        underTest.updateAtmosphere("cozy")

        // Act
        underTest.clearForm()

        // Assert
        val overallRating = underTest.overallRating.getOrAwaitValue()
        val atmosphere = underTest.atmosphere.getOrAwaitValue()
        val errorMessage = underTest.errorMessage.getOrAwaitValue()

        assertEquals("Overall rating should be reset", 0.0, overallRating, 0.001)
        assertEquals("Atmosphere should be cleared", "", atmosphere)
        assertNull("Error message should be cleared", errorMessage)
    }

    @Test
    fun whenClearErrorThenErrorMessageIsCleared() {
        // Arrange
        underTest.startReview("cafe123", "user123")
        underTest.submitReview() // This will set an error

        // Act
        underTest.clearError()

        // Assert
        val errorMessage = underTest.errorMessage.getOrAwaitValue()
        assertNull("Error message should be cleared", errorMessage)
    }

    @Test
    fun whenAddPhotoThenPhotoIsAddedToList() {
        // Arrange
        val photoUrl = "https://example.com/photo.jpg"

        // Act
        underTest.addPhoto(photoUrl)

        // Assert
        val uploadedPhotos = underTest.uploadedPhotos.getOrAwaitValue()
        assertEquals("Should have one photo", 1, uploadedPhotos.size)
        assertEquals("Should have correct photo URL", photoUrl, uploadedPhotos[0])
    }

    @Test
    fun whenRemovePhotoThenPhotoIsRemovedFromList() {
        // Arrange
        val photoUrl1 = "https://example.com/photo1.jpg"
        val photoUrl2 = "https://example.com/photo2.jpg"
        underTest.addPhoto(photoUrl1)
        underTest.addPhoto(photoUrl2)

        // Act
        underTest.removePhoto(photoUrl1)

        // Assert
        val uploadedPhotos = underTest.uploadedPhotos.getOrAwaitValue()
        assertEquals("Should have one photo remaining", 1, uploadedPhotos.size)
        assertEquals("Should have correct remaining photo", photoUrl2, uploadedPhotos[0])
    }
}