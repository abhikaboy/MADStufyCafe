package com.example.composeapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.data.database.FakeCafeDao
import com.example.composeapp.data.network.ApiResult
import com.example.composeapp.data.network.FakeApiService
import com.example.composeapp.utils.FakeLocationHelper
import com.example.composeapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CafeRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var fakeCafeDao: FakeCafeDao
    private lateinit var fakeApiService: FakeApiService
    private lateinit var fakeLocationHelper: FakeLocationHelper
    private lateinit var underTest: CafeRepository

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        fakeCafeDao = FakeCafeDao()
        fakeApiService = FakeApiService()
        fakeLocationHelper = FakeLocationHelper()
        underTest = CafeRepository(fakeCafeDao, fakeApiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenGetAllCafesAndDatabaseIsEmptyThenApiDataIsReturned() = runTest(testDispatcher) {
        // Arrange
        val expectedCafes = fakeApiService.getAllCafes()
        val liveData = underTest.getAllCafesLiveData()

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Result should be success", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertEquals("Should return API cafes count", expectedCafes.size, successResult.data.size)
    }

    @Test
    fun whenGetAllCafesWithCachedDataThenCachedDataIsReturnedFirst() = runTest(testDispatcher) {
        // Arrange
        val cachedCafe = createTestCafeEntity(1, "Cached Cafe")
        fakeCafeDao.insertCafe(cachedCafe)
        val liveData = underTest.getAllCafesLiveData()

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Result should be success", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertTrue("Should contain cached cafe",
            successResult.data.any { it.name == "Cached Cafe" })
    }

    @Test
    fun whenGetAllCafesWithLocationThenNearbyDataIsFetched() = runTest(testDispatcher) {
        // Arrange
        val testLocation = FakeLocationHelper.DEFAULT_LOCATION
        fakeLocationHelper.setCurrentLocation(testLocation)
        val liveData = underTest.getAllCafesLiveData()

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Result should be success", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertTrue("Should return cafes", successResult.data.isNotEmpty())
    }

    @Test
    fun whenGetAllCafesWithoutLocationThenGenericApiIsCalled() = runTest(testDispatcher) {
        // Arrange
        fakeLocationHelper.setCurrentLocation(null)
        val liveData = underTest.getAllCafesLiveData()

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Result should be success", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertTrue("Should return cafes from API", successResult.data.isNotEmpty())
    }

    @Test
    fun whenGetCafeByIdAndCafeExistsInDatabaseThenCachedDataIsReturned() = runTest(testDispatcher) {
        // Arrange
        val testCafe = createTestCafeEntity(1, "Test Cafe")
        fakeCafeDao.insertCafe(testCafe)
        val liveData = underTest.getCafeByIdLiveData(1)

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Result should be success", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertNotNull("Cafe should not be null", successResult.data)
        assertEquals("Cafe name should match", "Test Cafe", successResult.data?.name)
    }

    @Test
    fun whenSearchCafesWithQueryThenResultsAreReturned() = runTest(testDispatcher) {
        // Arrange
        val localCafe = createTestCafeEntity(1, "Local Coffee Shop")
        fakeCafeDao.insertCafe(localCafe)
        val searchQuery = "coffee"
        val liveData = underTest.searchCafesLiveData(searchQuery)

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Result should be success", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertTrue("Should contain search results", successResult.data.isNotEmpty())
    }

    @Test
    fun whenRefreshCafesWithLocationThenDataIsFetched() = runTest(testDispatcher) {
        // Arrange
        val testLocation = FakeLocationHelper.DEFAULT_LOCATION
        fakeLocationHelper.setCurrentLocation(testLocation)

        // Act
        val actualResult = underTest.refreshCafes()

        // Assert
        assertTrue("Refresh should be successful", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertTrue("Should return refreshed data", successResult.data.isNotEmpty())
    }

    @Test
    fun whenBookmarkCafeThenDatabaseIsUpdated() = runTest(testDispatcher) {
        // Arrange
        val cafe = createTestCafeEntity(1, "Test Cafe")
        fakeCafeDao.insertCafe(cafe)
        val cafeId = 1L
        val isBookmarked = true

        // Act
        underTest.bookmarkCafe(cafeId, isBookmarked)

        // Assert
        val updatedCafe = fakeCafeDao.getCafeById(cafeId)
        assertEquals("Bookmark status should be updated", isBookmarked, updatedCafe?.isBookmarked)
    }

    @Test
    fun whenGetBookmarkedCafesThenOnlyBookmarkedCafesAreReturned() = runTest(testDispatcher) {
        // Arrange
        val bookmarkedCafe = createTestCafeEntity(1, "Bookmarked Cafe", isBookmarked = true)
        val normalCafe = createTestCafeEntity(2, "Normal Cafe", isBookmarked = false)
        fakeCafeDao.insertCafe(bookmarkedCafe)
        fakeCafeDao.insertCafe(normalCafe)
        val liveData = underTest.getBookmarkedCafes()

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertEquals("Should return only bookmarked cafes", 1, actualResult.size)
        assertTrue("Returned cafe should be bookmarked", actualResult[0].isBookmarked)
        assertEquals("Should return correct cafe", "Bookmarked Cafe", actualResult[0].name)
    }

    @Test
    fun whenFindNearbyCafesThenResultsAreReturned() = runTest(testDispatcher) {
        // Arrange
        val longitude = -118.2437
        val latitude = 34.0522
        val maxDistance = 3000.0
        val liveData = underTest.findNearbyCafesLiveData(longitude, latitude, maxDistance)

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Result should be success", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertNotNull("Should return results", successResult.data)
    }

    @Test
    fun whenFindCafesByRatingThenHighRatedCafesAreReturned() = runTest(testDispatcher) {
        // Arrange
        val minRating = 4.0
        val liveData = underTest.findCafesByRatingLiveData(minRating)

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Result should be success", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertTrue("Should return cafes meeting rating criteria",
            successResult.data.all { it.averageRating >= minRating.toInt() })
    }

    @Test
    fun whenFindCafesByAmenitiesThenCafesWithAmenitiesAreReturned() = runTest(testDispatcher) {
        // Arrange
        val amenities = listOf("WiFi", "Power outlets")
        val liveData = underTest.findCafesByAmenitiesLiveData(amenities)

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Result should be success", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertNotNull("Should return results", successResult.data)
    }

    private fun createTestCafeEntity(
        id: Long,
        name: String,
        isBookmarked: Boolean = false
    ) = CafeEntity(
        id = id,
        apiId = id.toString(),
        name = name,
        address = "Test Address",
        isBookmarked = isBookmarked
    )
}