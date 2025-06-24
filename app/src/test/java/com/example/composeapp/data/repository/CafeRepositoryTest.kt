package com.example.composeapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.composeapp.data.database.CafeEntity
import com.example.composeapp.data.database.FakeCafeDao
import com.example.composeapp.data.network.ApiResult
import com.example.composeapp.data.network.FakeApiService
import com.example.composeapp.data.network.toEntity
import com.example.composeapp.utils.FakeLocationHelper
import com.example.composeapp.utils.LocationHelperInterface
import com.example.composeapp.utils.UserLocation
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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], manifest = Config.NONE)
class CafeRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var fakeCafeDao: FakeCafeDao
    private lateinit var fakeApiService: FakeApiService
    private lateinit var fakeLocationHelper: LocationHelperInterface
    private lateinit var underTest: CafeRepository

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)

        fakeCafeDao = FakeCafeDao()
        fakeApiService = FakeApiService()
        fakeLocationHelper = FakeLocationHelper()
        underTest = CafeRepository(fakeCafeDao, fakeApiService, fakeLocationHelper)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenGetAllCafesAndDatabaseIsEmptyThenApiDataIsReturned() = runTest(testDispatcher) {
        // Arrange - set location near test cafes so nearby API works
        val testLocation = UserLocation(latitude = 37.78, longitude = -122.03) // Near Palo Alto
        (fakeLocationHelper as FakeLocationHelper).setCurrentLocation(testLocation)
        val expectedCafes = fakeApiService.findNearbyCafes(testLocation.longitude, testLocation.latitude, 10000.0)
        val liveData = underTest.getAllCafesLiveData()

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Result should be success", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertEquals("Should return nearby cafes count", expectedCafes.size, successResult.data.size)
        assertTrue("Should return at least one cafe", successResult.data.isNotEmpty())
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
        // Arrange - use location near the test cafes in California
        val testLocation = UserLocation(latitude = 37.78, longitude = -122.03) // Near Palo Alto
        (fakeLocationHelper as FakeLocationHelper).setCurrentLocation(testLocation)
        val liveData = underTest.getAllCafesLiveData()

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Result should be success", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertTrue("Should return cafes", successResult.data.isNotEmpty())
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
        // Arrange - use location near the test cafes in California
        val testLocation = UserLocation(latitude = 37.78, longitude = -122.03) // Near Palo Alto
        (fakeLocationHelper as FakeLocationHelper).setCurrentLocation(testLocation)

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

    @Test
    fun debugTest_apiAndConversionWorking() = runTest(testDispatcher) {
        // Test if API returns data
        val apiCafes = fakeApiService.getAllCafes()
        assertTrue("API should return cafes", apiCafes.isNotEmpty())
        assertEquals("API should return 2 cafes", 2, apiCafes.size)
        
        // Test if conversion works
        val entities = apiCafes.map { it.toEntity() }
        assertEquals("Conversion should work", 2, entities.size)
        assertTrue("Converted entities should have names", entities.all { it.name.isNotEmpty() })
    }

    @Test
    fun debugTest_liveDataWithoutLocationHelper() = runTest(testDispatcher) {
        // Test with simpler repository without location helper
        val simpleRepository = CafeRepository(fakeCafeDao, fakeApiService, null)
        val liveData = simpleRepository.getAllCafesLiveData()
        
        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)
        
        // Debug output
        println("Debug: Result type = ${actualResult::class.simpleName}")
        if (actualResult.isSuccess) {
            val successResult = actualResult as ApiResult.Success
            println("Debug: Success data size = ${successResult.data.size}")
        } else if (actualResult.isError) {
            val errorResult = actualResult as ApiResult.Error
            println("Debug: Error message = ${errorResult.message}")
        }
        
        // Assert
        assertTrue("Result should be success", actualResult.isSuccess)
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
        isBookmarked = isBookmarked,
        latitude = 40.7128,
        longitude = -74.0060,
        studyRating = 4,
        averageRating = 4,
        wifiQuality = "Good",
        powerOutlets = "Many"
    )
}