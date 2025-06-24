package com.example.composeapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.composeapp.data.network.*
import com.example.composeapp.data.network.fakes.FakeApiService
import com.example.composeapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE)
class ReviewRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeApiService: FakeApiService
    private lateinit var underTest: ReviewRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        fakeApiService = FakeApiService()
        underTest = ReviewRepository(fakeApiService)
    }

    @Test
    fun whenCreateReviewWithValidDataThenReviewIsCreated() = runTest {
        // Arrange
        val reviewCreate = ReviewCreate(
            study_spot_id = "1",
            user_id = "1",
            overall_rating = 4.5,
            outlet_accessibility = 4.0,
            wifi_quality = 4.5,
            atmosphere = "cozy",
            energy_level = "calm",
            study_friendly = "yes"
        )

        // Act
        val actualResult = underTest.createReview(reviewCreate)
        // Assert
        assertTrue("Review creation should be successful", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertEquals("Should return correct study spot ID", reviewCreate.study_spot_id, successResult.data.study_spot_id)
        assertEquals("Should return correct user ID", reviewCreate.user_id, successResult.data.user_id)
        assertEquals("Should return correct overall rating", reviewCreate.overall_rating, successResult.data.overall_rating, 0.001)
        assertEquals("Should return correct atmosphere", reviewCreate.atmosphere, successResult.data.atmosphere)
    }

    @Test
    fun whenGetReviewByIdWithExistingReviewThenReviewIsReturned() = runTest {
        // Arrange
        val review = fakeApiService.createReview(
            ReviewCreate("1", "1", 4.0, 3.5, 4.0)
        )
        val liveData = underTest.getReviewById(review.id)
        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Should return review successfully", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertEquals("Should return correct review ID", review.id, successResult.data.id)
        assertEquals("Should return correct study spot ID", review.study_spot_id, successResult.data.study_spot_id)
    }

    @Test
    fun whenGetReviewByIdWithNonExistentReviewThenErrorIsReturned() = runTest {
        // Arrange
        val nonExistentReviewId = "999"
        val liveData = underTest.getReviewById(nonExistentReviewId)
        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)
        // Assert
        assertTrue("Should return error", actualResult.isError)
        val errorResult = actualResult as ApiResult.Error
        assertTrue("Should contain review not found message",
            errorResult.message.contains("Review not found"))
    }

    @Test
    fun whenUpdateReviewThenReviewIsUpdatedSuccessfully() = runTest {
        // Arrange
        val review = fakeApiService.createReview(
            ReviewCreate("1", "1", 3.0, 3.0, 3.0)
        )
        val reviewUpdate = ReviewUpdate(
            overall_rating = 5.0,
            atmosphere = "excellent",
            study_friendly = "perfect"
        )
        // Act
        val actualResult = underTest.updateReview(review.id, reviewUpdate)

        // Assert
        assertTrue("Update should be successful", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertEquals("Should return updated overall rating", 5.0, successResult.data.overall_rating, 0.001)
        assertEquals("Should return updated atmosphere", "excellent", successResult.data.atmosphere)
        assertEquals("Should return updated study friendly", "perfect", successResult.data.study_friendly)
    }

    @Test
    fun whenDeleteReviewThenReviewIsDeletedSuccessfully() = runTest {
        // Arrange
        val review = fakeApiService.createReview(
            ReviewCreate("1", "1", 4.0, 4.0, 4.0)
        )
        // Act
        val actualResult = underTest.deleteReview(review.id)
        // Assert
        assertTrue("Delete should be successful", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertEquals("Should return success status", "success", successResult.data["status"])
    }

    @Test
    fun whenGetReviewsByStudySpotThenStudySpotReviewsAreReturned() = runTest {
        // Arrange
        val studySpotId = "test_spot_1"
        fakeApiService.createReview(ReviewCreate(studySpotId, "user1", 4.0, 4.0, 4.0))
        fakeApiService.createReview(ReviewCreate(studySpotId, "user2", 3.5, 3.5, 3.5))
        fakeApiService.createReview(ReviewCreate("other_spot", "user1", 5.0, 5.0, 5.0))
        val liveData = underTest.getReviewsByStudySpot(studySpotId)

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Should return reviews successfully", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertEquals("Should return 2 reviews for study spot", 2, successResult.data.size)
        assertTrue("All reviews should be for correct study spot",
            successResult.data.all { it.study_spot_id == studySpotId })
    }

    @Test
    fun whenGetReviewsByUserThenUserReviewsAreReturned() = runTest {
        // Arrange
        val userId = "test_user_1"
        fakeApiService.createReview(ReviewCreate("spot1", userId, 4.0, 4.0, 4.0))
        fakeApiService.createReview(ReviewCreate("spot2", userId, 3.5, 3.5, 3.5))
        fakeApiService.createReview(ReviewCreate("spot1", "other_user", 5.0, 5.0, 5.0))
        val liveData = underTest.getReviewsByUser(userId)

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Should return reviews successfully", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertEquals("Should return 2 reviews for user", 2, successResult.data.size)
        assertTrue("All reviews should be for correct user",
            successResult.data.all { it.user_id == userId })
    }

    @Test
    fun whenAddPhotoToReviewThenPhotoIsAddedSuccessfully() = runTest {
        // Arrange
        val review = fakeApiService.createReview(
            ReviewCreate("1", "1", 4.0, 4.0, 4.0)
        )
        val photo = PhotoCreate(
            url = "https://example.com/photo.jpg",
            caption = "Great atmosphere"
        )
        // Act
        val actualResult = underTest.addPhotoToReview(review.id, photo)

        // Assert
        assertTrue("Photo addition should be successful", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertTrue("Review should have photos", successResult.data.photos.isNotEmpty())
        val addedPhoto = successResult.data.photos.last()
        assertEquals("Should have correct photo URL", photo.url, addedPhoto.url)
        assertEquals("Should have correct caption", photo.caption, addedPhoto.caption)
    }
}