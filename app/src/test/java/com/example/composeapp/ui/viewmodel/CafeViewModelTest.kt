package com.example.composeapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.data.repository.fakes.FakeCafeRepository
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
class CafeViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var fakeCafeRepository: FakeCafeRepository
    private lateinit var underTest: CafeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        fakeCafeRepository = FakeCafeRepository()
        underTest = CafeViewModel(
            repository = fakeCafeRepository
        )
    }

    @Test
    fun whenViewModelIsInitializedThenRefreshCafesIsCalled() {
        // Assert
        assertTrue("Refresh should have been called on init",
            fakeCafeRepository.refreshCafesWasCalled)
    }

    @Test
    fun whenSearchCafesWithQueryThenSearchQueryIsUpdated() {
        // Arrange
        val searchQuery = "coffee shop"
        // Act
        underTest.searchCafes(searchQuery)

        // Assert
        val searchResults = underTest.searchResults.getOrAwaitValue()
        assertEquals("Search query should be updated", searchQuery,
            fakeCafeRepository.lastSearchQuery)
    }

    @Test
    fun whenSelectCafeWithIdThenSelectedCafeIdIsUpdated() {
        // Arrange
        val cafeId = 123L
        // Act
        underTest.selectCafe(cafeId)

        // Assert
        val selectedCafe = underTest.selectedCafe.getOrAwaitValue()
        assertEquals("Selected cafe ID should be updated", cafeId,
            fakeCafeRepository.lastSelectedCafeId)
    }

    @Test
    fun whenFindNearbyCafesWithLocationThenNearbySearchIsCalled() {
        // Arrange
        val longitude = -122.4194
        val latitude = 37.7749
        val maxDistance = 5000.0

        // Act
        underTest.findNearbyCafes(longitude, latitude, maxDistance)
        // Assert
        val nearbyCafes = underTest.nearbyCafes.getOrAwaitValue()
        assertTrue("Nearby search should have been called",
            fakeCafeRepository.findNearbyCafesWasCalled)
        assertEquals("Should use correct longitude", longitude,
            fakeCafeRepository.lastLongitude, 0.001)
        assertEquals("Should use correct latitude", latitude,
            fakeCafeRepository.lastLatitude, 0.001)
        assertEquals("Should use correct max distance", maxDistance,
            fakeCafeRepository.lastMaxDistance, 0.001)
    }

    @Test
    fun whenFindCafesByRatingWithMinRatingThenRatingSearchIsCalled() {
        // Arrange
        val minRating = 4.5

        // Act
        underTest.findCafesByRating(minRating)
        // Assert
        val cafesByRating = underTest.cafesByRating.getOrAwaitValue()
        assertTrue("Rating search should have been called",
            fakeCafeRepository.findCafesByRatingWasCalled)
        assertEquals("Should use correct min rating", minRating,
            fakeCafeRepository.lastMinRating, 0.001)
    }

    @Test
    fun whenFindCafesByAmenitiesThenAmenitiesSearchIsCalled() {
        // Arrange
        val amenities = listOf("WiFi", "Power outlets", "Quiet zone")

        // Act
        underTest.findCafesByAmenities(amenities)

        // Assert
        val cafesByAmenities = underTest.cafesByAmenities.getOrAwaitValue()
        assertTrue("Amenities search should have been called",
            fakeCafeRepository.findCafesByAmenitiesWasCalled)
        assertEquals("Should use correct amenities", amenities,
            fakeCafeRepository.lastAmenities)
    }

    @Test
    fun whenBookmarkCafeThenBookmarkStatusIsUpdated() = runTest {
        // Arrange
        val cafeId = 1L
        val isBookmarked = true

        // Act
        underTest.bookmarkCafe(cafeId, isBookmarked)
        // Assert
        assertTrue("Bookmark should have been updated",
            fakeCafeRepository.bookmarkCafeWasCalled)
        assertEquals("Should use correct cafe ID", cafeId,
            fakeCafeRepository.lastBookmarkCafeId)
        assertEquals("Should use correct bookmark status", isBookmarked,
            fakeCafeRepository.lastBookmarkStatus)
    }

    @Test
    fun whenRefreshCafesThenLoadingStateIsManaged() = runTest {
        // Arrange
        fakeCafeRepository.shouldReturnError = false

        // Act
        underTest.refreshCafes()
        // Assert
        val isRefreshing = underTest.isRefreshing.getOrAwaitValue()
        val isLoading = underTest.isLoading.getOrAwaitValue()
        val errorMessage = underTest.errorMessage.getOrAwaitValue()

        assertFalse("Should not be refreshing after completion", isRefreshing)
        assertFalse("Should not be loading after completion", isLoading)
        assertNull("Should have no error message on success", errorMessage)
    }

    @Test
    fun whenRefreshCafesFailsThenErrorMessageIsSet() = runTest {
        // Arrange
        fakeCafeRepository.shouldReturnError = true
        fakeCafeRepository.errorMessage = "Network error"
        // Act
        underTest.refreshCafes()
        // Assert
        val errorMessage = underTest.errorMessage.getOrAwaitValue()
        assertEquals("Should show error message", "Network error", errorMessage)
    }

    @Test
    fun whenClearErrorThenErrorMessageIsCleared() {
        // Arrange
        fakeCafeRepository.shouldReturnError = true
        underTest.refreshCafes()
        // Act
        underTest.clearError()
        // Assert
        val errorMessage = underTest.errorMessage.getOrAwaitValue()
        assertNull("Error message should be cleared", errorMessage)
    }

    @Test
    fun whenClearSearchThenSearchQueryIsCleared() {
        // Arrange
        underTest.searchCafes("test query")

        // Act
        underTest.clearSearch()
        // Assert
        assertEquals("Search query should be cleared", "",
            fakeCafeRepository.lastSearchQuery)
    }

    @Test
    fun whenGetCafeCountWithLoadedCafesThenCorrectCountIsReturned() {
        // Arrange
        fakeCafeRepository.setCafes(listOf(
            createTestCafeEntity(1, "Cafe 1"),
            createTestCafeEntity(2, "Cafe 2"),
            createTestCafeEntity(3, "Cafe 3")
        ))

        // Act
        val count = underTest.getCafeCount()
        // Assert
        assertEquals("Should return correct cafe count", 3, count)
    }

    @Test
    fun whenIsCafesLoadedWithLoadedCafesThenReturnsTrue() {
        // Arrange
        fakeCafeRepository.setCafes(listOf(createTestCafeEntity(1, "Cafe 1")))
        // Act
        val isLoaded = underTest.isCafesLoaded()

        // Assert
        assertTrue("Should return true when cafes are loaded", isLoaded)
    }

    private fun createTestCafeEntity(id: Long, name: String) = CafeEntity(
        id = id,
        apiId = id.toString(),
        name = name,
        address = "Test Address"
    )
}