package com.example.composeapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.composeapp.data.network.*
import com.example.composeapp.utils.TestDataFactory
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
class UserRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var fakeApiService: FakeApiService
    private lateinit var underTest: UserRepository

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        fakeApiService = FakeApiService()
        underTest = UserRepository(fakeApiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenLoginWithValidCredentialsThenLoginIsSuccessful() = runTest(testDispatcher) {
        // Arrange
        val username = "Test User 1" // This user exists in your FakeApiService
        val password = "password123"
        val liveData = underTest.loginUser(username, password)

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Login should be successful", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertEquals("Should return success message", "Login successful", successResult.data.message)
        assertEquals("Should return correct username", username, successResult.data.user_name)
    }

    @Test
    fun whenLoginWithInvalidCredentialsThenLoginFails() = runTest(testDispatcher) {
        // Arrange
        val username = "invalid_user"
        val password = "wrong_password"
        val liveData = underTest.loginUser(username, password)

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Login should fail", actualResult.isError)
        val errorResult = actualResult as ApiResult.Error
        assertTrue("Should contain error message", errorResult.message.contains("Invalid credentials"))
    }

    @Test
    fun whenCreateUserWithValidDataThenUserIsCreated() = runTest(testDispatcher) {
        // Arrange
        val name = "New User"
        val password = "securePassword123"
        val cafesVisited = 3
        val averageRating = 4.2
        val liveData = underTest.createUser(name, password, cafesVisited, averageRating)

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("User creation should be successful", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertEquals("Should return correct name", name, successResult.data.name)
        assertEquals("Should return correct cafes visited", cafesVisited, successResult.data.cafes_visited)
        assertEquals("Should return correct average rating", averageRating, successResult.data.average_rating, 0.001)
    }

    @Test
    fun whenGetUserByIdWithExistingUserThenUserIsReturned() = runTest(testDispatcher) {
        // Arrange
        val existingUserId = "1" // From FakeApiService test data
        val liveData = underTest.getUserById(existingUserId)

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Should return user successfully", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertEquals("Should return correct user ID", existingUserId, successResult.data.id)
        assertNotNull("User name should not be null", successResult.data.name)
    }

    @Test
    fun whenGetUserByIdWithNonExistentUserThenErrorIsReturned() = runTest(testDispatcher) {
        // Arrange
        val nonExistentUserId = "999"
        val liveData = underTest.getUserById(nonExistentUserId)

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Should return error", actualResult.isError)
        val errorResult = actualResult as ApiResult.Error
        assertTrue("Should contain user not found message",
            errorResult.message.contains("User not found"))
    }

    @Test
    fun whenUpdateUserThenUserIsUpdatedSuccessfully() = runTest(testDispatcher) {
        // Arrange
        val userId = "1"
        val updatedName = "Updated Name"
        val updatedCafesVisited = 10
        val userUpdate = UserUpdate(name = updatedName, cafes_visited = updatedCafesVisited)

        // Act
        val actualResult = underTest.updateUser(userId, userUpdate)

        // Assert
        assertTrue("Update should be successful", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertEquals("Should return updated name", updatedName, successResult.data.name)
        assertEquals("Should return updated cafes visited", updatedCafesVisited, successResult.data.cafes_visited)
    }

    @Test
    fun whenSearchUsersWithQueryThenMatchingUsersAreReturned() = runTest(testDispatcher) {
        // Arrange
        val searchQuery = "Test User"
        val liveData = underTest.searchUsers(searchQuery)

        // Act
        val actualResult = liveData.getOrAwaitValue(time = 2)

        // Assert
        assertTrue("Search should be successful", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertTrue("Should return matching users", successResult.data.isNotEmpty())
        assertTrue("All users should match query",
            successResult.data.all { it.name.contains(searchQuery, ignoreCase = true) })
    }

    @Test
    fun whenCreateBookmarkThenBookmarkIsCreatedSuccessfully() = runTest(testDispatcher) {
        // Arrange - use existing test data from FakeApiService
        val userId = "1" // User that already exists in FakeApiService
        val cafeId = "1" // Cafe that already exists in FakeApiService

        // Act
        val actualResult = underTest.createBookmark(userId, cafeId)

        // Assert
        assertTrue("Bookmark creation should be successful", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertEquals("Should return correct user ID", userId, successResult.data.user_id)
        assertEquals("Should return correct cafe ID", cafeId, successResult.data.cafe_id)
    }

    @Test
    fun whenDeleteBookmarkThenBookmarkIsDeletedSuccessfully() = runTest(testDispatcher) {
        // Arrange - use existing test data
        val userId = "1"
        val cafeId = "1"
        // Create bookmark first
        underTest.createBookmark(userId, cafeId)

        // Act
        val actualResult = underTest.deleteBookmark(userId, cafeId)

        // Assert
        assertTrue("Bookmark deletion should be successful", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertEquals("Should return success status", "success", successResult.data["status"])
    }

    @Test
    fun whenCheckBookmarkExistsForExistingBookmarkThenTrueIsReturned() = runTest(testDispatcher) {
        // Arrange - use existing test data
        val userId = "1"
        val cafeId = "1"
        // Create bookmark first
        underTest.createBookmark(userId, cafeId)

        // Act
        val actualResult = underTest.checkBookmarkExists(userId, cafeId)

        // Assert
        assertTrue("Check should be successful", actualResult.isSuccess)
        val successResult = actualResult as ApiResult.Success
        assertTrue("Bookmark should exist", successResult.data["exists"] == true)
    }

    @Test
    fun whenBookmarkWorkflowIsCompleteThenBookmarkExistsAndCanBeDeleted() = runTest(testDispatcher) {
        // Arrange - use existing test data
        val userId = "1"
        val cafeId = "1"
        
        // Act & Assert - Create bookmark
        val createResult = underTest.createBookmark(userId, cafeId)
        assertTrue("Bookmark creation should succeed", createResult.isSuccess)
        
        // Act & Assert - Check bookmark exists
        val checkResult = underTest.checkBookmarkExists(userId, cafeId)
        assertTrue("Check bookmark should succeed", checkResult.isSuccess)
        val checkData = checkResult as ApiResult.Success
        assertTrue("Bookmark should exist", checkData.data["exists"] == true)
        
        // Act & Assert - Delete bookmark
        val deleteResult = underTest.deleteBookmark(userId, cafeId)
        assertTrue("Delete bookmark should succeed", deleteResult.isSuccess)
        val deleteData = deleteResult as ApiResult.Success
        assertEquals("Should return success status", "success", deleteData.data["status"])
    }
}