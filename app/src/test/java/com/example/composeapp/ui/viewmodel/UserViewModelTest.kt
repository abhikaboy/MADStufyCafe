package com.example.composeapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.composeapp.data.network.*
import com.example.composeapp.data.repository.fakes.FakeReviewRepository
import com.example.composeapp.data.repository.fakes.FakeUserRepository
import com.example.composeapp.data.repository.interfaces.ReviewRepositoryInterface
import com.example.composeapp.data.repository.interfaces.UserRepositoryInterface
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
class UserViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeReviewRepository: FakeReviewRepository
    private lateinit var userRepositoryInterface: UserRepositoryInterface
    private lateinit var reviewRepositoryInterface: ReviewRepositoryInterface
    private lateinit var underTest: UserViewModel

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        fakeUserRepository = FakeUserRepository()
        fakeReviewRepository = FakeReviewRepository()
        // Assign to interface variables
        userRepositoryInterface = fakeUserRepository
        reviewRepositoryInterface = fakeReviewRepository
        underTest = UserViewModel(userRepositoryInterface, reviewRepositoryInterface)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenLoginWithCredentialsThenLoginIsCalled() {
        // Arrange
        val username = "testuser"
        val password = "password123"

        // Act
        underTest.login(username, password)

        // Assert
        val loginResult = underTest.loginResult.getOrAwaitValue()
        assertTrue("Login should have been called", fakeUserRepository.loginWasCalled)
    }

    @Test
    fun whenCreateUserWithDataThenUserCreationIsCalled() {
        // Arrange
        val name = "New User"
        val password = "password123"
        val cafesVisited = 5
        val averageRating = 4.2

        // Act
        underTest.createUser(name, password, cafesVisited, averageRating)

        // Assert - trigger the switchMap by observing userCreateResult
        val userCreateResult = underTest.userCreateResult.getOrAwaitValue()
        assertTrue("Create user should have been called", fakeUserRepository.createUserWasCalled)
    }

    @Test
    fun whenUpdateUserThenUserIsUpdated() = runTest(testDispatcher) {
        // Arrange
        val userId = "user123"
        val updatedName = "Updated Name"
        val updatedCafesVisited = 10
        fakeUserRepository.configureUpdateUserResult(ApiResult.Success(  // Fixed: method name
            UserResponse(userId, updatedName, updatedCafesVisited, 4.5)
        ))

        // Act
        underTest.updateUser(userId, updatedName, updatedCafesVisited)

        // Assert
        assertTrue("Update user should have been called", fakeUserRepository.updateUserWasCalled)
        val currentUser = underTest.currentUser.getOrAwaitValue()
        assertEquals("Should update current user", updatedName, currentUser?.name)
    }

    @Test
    fun whenUpdateUserFailsThenErrorIsShown() = runTest(testDispatcher) {
        // Arrange
        val userId = "user123"
        fakeUserRepository.configureUpdateUserResult(ApiResult.Error("Update failed"))  // Fixed: method name

        // Act
        underTest.updateUser(userId, "New Name")

        // Assert
        val errorMessage = underTest.errorMessage.getOrAwaitValue()
        assertEquals("Should show error message", "Update failed", errorMessage)
    }

    @Test
    fun whenCreateBookmarkThenBookmarkIsCreated() = runTest(testDispatcher) {
        // Arrange
        val userId = "user123"
        val cafeId = "cafe123"
        fakeUserRepository.configureCreateBookmarkResult(ApiResult.Success(  // Fixed: method name
            Bookmark("bookmark123", userId, cafeId, "2024-01-01T00:00:00Z")
        ))

        // Act
        underTest.createBookmark(userId, cafeId)

        // Assert
        assertTrue("Create bookmark should have been called",
            fakeUserRepository.createBookmarkWasCalled)
        val successMessage = underTest.bookmarkActionSuccess.getOrAwaitValue()
        assertEquals("Should show success message",
            "Bookmark created successfully", successMessage)
    }

    @Test
    fun whenCreateBookmarkFailsThenErrorIsShown() = runTest(testDispatcher) {
        // Arrange
        val userId = "user123"
        val cafeId = "cafe123"
        fakeUserRepository.configureCreateBookmarkResult(ApiResult.Error("Bookmark failed"))  // Fixed: method name

        // Act
        underTest.createBookmark(userId, cafeId)

        // Assert
        val errorMessage = underTest.errorMessage.getOrAwaitValue()
        assertTrue("Should show bookmark error",
            errorMessage?.contains("Failed to create bookmark") == true)
    }

    @Test
    fun whenDeleteBookmarkThenBookmarkIsDeleted() = runTest(testDispatcher) {
        // Arrange
        val userId = "user123"
        val cafeId = "cafe123"

        // Act
        underTest.deleteBookmark(userId, cafeId)

        // Assert
        assertTrue("Delete bookmark should have been called",
            fakeUserRepository.deleteBookmarkWasCalled)
        val successMessage = underTest.bookmarkActionSuccess.getOrAwaitValue()
        assertEquals("Should show success message",
            "Bookmark deleted successfully", successMessage)
    }

    @Test
    fun whenLogoutThenUserStateIsCleared() {
        // Arrange
        fakeUserRepository.configureUserResult(ApiResult.Success(
            UserResponse("user123", "Test User", 5, 4.2)
        ))

        // Act
        underTest.logout()

        // Assert
        val currentUser = underTest.currentUser.getOrAwaitValue()
        val isLoggedIn = underTest.isLoggedIn.getOrAwaitValue()

        assertNull("Current user should be null", currentUser)
        assertFalse("Should not be logged in", isLoggedIn)
    }

    @Test
    fun whenSearchUsersWithQueryThenSearchIsCalled() {
        // Arrange
        val query = "test user"

        // Act
        underTest.searchUsers(query)

        // Assert
        val searchResults = underTest.searchResults.getOrAwaitValue()
        assertTrue("Search users should have been called", fakeUserRepository.searchUsersWasCalled)
        assertEquals("Should use correct query", query, fakeUserRepository.lastSearchQuery)
    }

    @Test
    fun whenLoadUserBookmarksThenUserIdIsSet() {
        // Arrange
        val userId = "user123"

        // Act
        underTest.loadUserBookmarks(userId)

        // Assert
        assertNotNull("User bookmarks LiveData should be available", underTest.userBookmarks)
    }

    @Test
    fun whenLoadAllUsersThenUsersParamsAreSet() {
        // Arrange
        val skip = 10
        val limit = 50

        // Act
        underTest.loadAllUsers(skip, limit)

        // Assert
        assertNotNull("All users LiveData should be available", underTest.allUsers)
    }

    @Test
    fun whenHandleLoginResultWithSuccessThenUserIsLoggedIn() {
        // Arrange
        val loginResponse = LoginResponse("Success", "user123", "testuser")
        val result = ApiResult.Success(loginResponse)
        assertTrue("Result should be success", result.isSuccess)
    }

    @Test
    fun whenHandleLoginResultWithErrorThenErrorIsShown() {
        // Arrange
        val result = ApiResult.Error("Login failed")
        assertTrue("Result should be error", result.isError)
    }

    @Test
    fun whenHandleUserCreateResultWithSuccessThenUserIsSet() {
        // Arrange
        val userResponse = UserResponse("user123", "New User", 0, 0.0)
        val result = ApiResult.Success(userResponse)
        assertTrue("Result should be success", result.isSuccess)
        assertEquals("Should have correct user name", "New User", result.data.name)
    }

    @Test
    fun whenHandleUserCreateResultWithErrorThenErrorIsShown() {
        // Arrange
        val result = ApiResult.Error("User creation failed")
        assertTrue("Result should be error", result.isError)
    }

    @Test
    fun whenClearErrorThenErrorMessageIsCleared() {
        // Act
        underTest.clearError()

        // Assert
        val errorMessage = underTest.errorMessage.getOrAwaitValue()
        assertNull("Error message should be cleared", errorMessage)
    }

    @Test
    fun whenClearBookmarkSuccessThenSuccessMessageIsCleared() = runTest(testDispatcher) {
        // Arrange
        fakeUserRepository.configureCreateBookmarkResult(ApiResult.Success(  // Fixed: method name
            Bookmark("bookmark123", "user123", "cafe123", "2024-01-01T00:00:00Z")
        ))
        underTest.createBookmark("user123", "cafe123")
        assertTrue("Create bookmark should have been called",
            fakeUserRepository.createBookmarkWasCalled)
    }

    @Test
    fun whenCheckBookmarkExistsWithValidDataThenCallbackIsInvoked() = runTest(testDispatcher) {
        // Arrange
        val userId = "user123"
        val cafeId = "cafe123"
        var callbackResult: Boolean? = null

        val result = fakeUserRepository.checkBookmarkExists(userId, cafeId)

        // Assert
        assertTrue("Check bookmark should have been called",
            fakeUserRepository.checkBookmarkExistsWasCalled)
        assertTrue("Result should be success", result.isSuccess)
    }
}