package com.example.composeapp.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.composeapp.data.network.ApiResult
import com.example.composeapp.data.network.LoginResponse
import com.example.composeapp.data.network.UserResponse
import com.example.composeapp.data.repository.FakeUserRepository
import com.example.composeapp.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var testDispatcher: TestDispatcher
    private lateinit var fakeUserRepository: FakeUserRepository  // Fixed: Correct type
    private lateinit var underTest: LoginViewModel

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)
        fakeUserRepository = FakeUserRepository()
        underTest = LoginViewModel(fakeUserRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenLoginWithValidCredentialsThenUserIsLoggedIn() {
        // Arrange
        val username = "testuser"
        val password = "password123"
        fakeUserRepository.configureLoginResult(ApiResult.Success(  // Fixed: method name
            LoginResponse("Login successful", "user123", username)
        ))
        fakeUserRepository.configureUserResult(ApiResult.Success(  // Fixed: method name
            UserResponse("user123", username, 5, 4.2)
        ))

        // Act
        underTest.login(username, password)

        // Assert
        val isLoggedIn = underTest.isLoggedIn.getOrAwaitValue()
        val currentUser = underTest.currentUser.getOrAwaitValue()
        val errorMessage = underTest.errorMessage.getOrAwaitValue()

        assertTrue("User should be logged in", isLoggedIn)
        assertNotNull("Current user should be set", currentUser)
        assertEquals("Should have correct username", username, currentUser?.name)
        assertNull("Should have no error message", errorMessage)
    }

    @Test
    fun whenLoginWithInvalidCredentialsThenErrorIsShown() {
        // Arrange
        val username = "wronguser"
        val password = "wrongpassword"
        fakeUserRepository.configureLoginResult(ApiResult.Error("Invalid credentials"))  // Fixed: method name

        // Act
        underTest.login(username, password)

        // Assert
        val isLoggedIn = underTest.isLoggedIn.getOrAwaitValue()
        val currentUser = underTest.currentUser.getOrAwaitValue()
        val errorMessage = underTest.errorMessage.getOrAwaitValue()

        assertFalse("User should not be logged in", isLoggedIn)
        assertNull("Current user should be null", currentUser)
        assertEquals("Should show error message", "Invalid credentials", errorMessage)
    }

    @Test
    fun whenLoginWithBlankUsernameThenValidationErrorIsShown() {
        // Arrange
        val username = ""
        val password = "password123"

        // Act
        underTest.login(username, password)

        // Assert
        val errorMessage = underTest.errorMessage.getOrAwaitValue()
        assertEquals("Should show username validation error",
            "Please enter a username", errorMessage)
        assertFalse("Login should not have been called", fakeUserRepository.loginWasCalled)
    }

    @Test
    fun whenLoginWithBlankPasswordThenValidationErrorIsShown() {
        // Arrange
        val username = "testuser"
        val password = ""

        // Act
        underTest.login(username, password)

        // Assert
        val errorMessage = underTest.errorMessage.getOrAwaitValue()
        assertEquals("Should show password validation error",
            "Please enter a password", errorMessage)
        assertFalse("Login should not have been called", fakeUserRepository.loginWasCalled)
    }

    @Test
    fun whenLoginWithWhitespaceUsernameThenUsernameIsTrimmed() {
        // Arrange
        val username = "  testuser  "
        val password = "password123"
        fakeUserRepository.configureLoginResult(ApiResult.Success(  // Fixed: method name
            LoginResponse("Login successful", "user123", "testuser")
        ))

        // Act
        underTest.login(username, password)

        // Assert
        assertTrue("Login should have been called", fakeUserRepository.loginWasCalled)
    }

    @Test
    fun whenLogoutThenUserStateIsCleared() {
        // Arrange - first login
        val username = "testuser"
        fakeUserRepository.configureLoginResult(ApiResult.Success(  // Fixed: method name
            LoginResponse("Login successful", "user123", username)
        ))
        fakeUserRepository.configureUserResult(ApiResult.Success(  // Fixed: method name
            UserResponse("user123", username, 5, 4.2)
        ))
        underTest.login(username, "password123")

        // Act
        underTest.logout()

        // Assert
        val isLoggedIn = underTest.isLoggedIn.getOrAwaitValue()
        val currentUser = underTest.currentUser.getOrAwaitValue()

        assertFalse("User should not be logged in", isLoggedIn)
        assertNull("Current user should be null", currentUser)
    }

    @Test
    fun whenClearErrorThenErrorMessageIsCleared() {
        // Arrange
        fakeUserRepository.configureLoginResult(ApiResult.Error("Test error"))  // Fixed: method name
        underTest.login("user", "pass")

        // Act
        underTest.clearError()

        // Assert
        val errorMessage = underTest.errorMessage.getOrAwaitValue()
        assertNull("Error message should be cleared", errorMessage)
    }

    @Test
    fun whenCheckLoginStatusWithCurrentUserThenIsLoggedInIsTrue() {
        // Arrange
        fakeUserRepository.configureLoginResult(ApiResult.Success(  // Fixed: method name
            LoginResponse("Login successful", "user123", "testuser")
        ))
        fakeUserRepository.configureUserResult(ApiResult.Success(  // Fixed: method name
            UserResponse("user123", "testuser", 5, 4.2)
        ))
        underTest.login("testuser", "password123")

        // Act
        underTest.checkLoginStatus()

        // Assert
        val isLoggedIn = underTest.isLoggedIn.getOrAwaitValue()
        assertTrue("Should be logged in when user exists", isLoggedIn)
    }

    @Test
    fun whenCheckLoginStatusWithoutCurrentUserThenIsLoggedInIsFalse() {
        // Arrange - no login performed

        // Act
        underTest.checkLoginStatus()

        // Assert
        val isLoggedIn = underTest.isLoggedIn.getOrAwaitValue()
        assertFalse("Should not be logged in when no user", isLoggedIn)
    }

    @Test
    fun whenLoginFailsToFetchUserDetailsThenErrorIsShown() {
        // Arrange
        fakeUserRepository.configureLoginResult(ApiResult.Success(  // Fixed: method name
            LoginResponse("Login successful", "user123", "testuser")
        ))
        fakeUserRepository.configureUserResult(ApiResult.Error("Failed to load user"))  // Fixed: method name

        // Act
        underTest.login("testuser", "password123")

        // Assert
        val isLoggedIn = underTest.isLoggedIn.getOrAwaitValue()
        val errorMessage = underTest.errorMessage.getOrAwaitValue()

        assertFalse("Should not be logged in", isLoggedIn)
        assertTrue("Should show user details error",
            errorMessage?.contains("Failed to load user details") == true)
    }

    @Test
    fun whenLoginIsCalledThenLoadingStateIsManaged() {
        // Arrange
        fakeUserRepository.configureLoginResult(ApiResult.Success(  // Fixed: method name
            LoginResponse("Login successful", "user123", "testuser")
        ))
        fakeUserRepository.configureUserResult(ApiResult.Success(  // Fixed: method name
            UserResponse("user123", "testuser", 5, 4.2)
        ))

        // Act
        underTest.login("testuser", "password123")

        // Assert
        val isLoading = underTest.isLoading.getOrAwaitValue()
        assertFalse("Should not be loading after completion", isLoading)
    }

    @Test
    fun whenLoginSucceedsThenLoadingIsFalse() {
        // Arrange
        fakeUserRepository.configureLoginResult(ApiResult.Success(  // Fixed: method name
            LoginResponse("Login successful", "user123", "testuser")
        ))
        fakeUserRepository.configureUserResult(ApiResult.Success(  // Fixed: method name
            UserResponse("user123", "testuser", 5, 4.2)
        ))

        // Act
        underTest.login("testuser", "password123")

        // Assert
        val isLoading = underTest.isLoading.getOrAwaitValue()
        val isLoggedIn = underTest.isLoggedIn.getOrAwaitValue()

        assertFalse("Should not be loading", isLoading)
        assertTrue("Should be logged in", isLoggedIn)
    }

    @Test
    fun whenLoginWithEmptyCredentialsThenNoRepositoryCallIsMade() {
        // Arrange
        val username = ""
        val password = ""

        // Act
        underTest.login(username, password)

        // Assert
        assertFalse("Repository login should not be called with empty username",
            fakeUserRepository.loginWasCalled)
    }

    @Test
    fun whenLoginWithOnlyWhitespaceThenValidationErrorIsShown() {
        // Arrange
        val username = "   "
        val password = "password123"

        // Act
        underTest.login(username, password)

        // Assert
        val errorMessage = underTest.errorMessage.getOrAwaitValue()
        assertEquals("Should show username validation error",
            "Please enter a username", errorMessage)
        assertFalse("Login should not have been called", fakeUserRepository.loginWasCalled)
    }

    @Test
    fun whenMultipleLoginAttemptsThenPreviousErrorIsCleared() {
        // Arrange
        fakeUserRepository.configureLoginResult(ApiResult.Error("First error"))  // Fixed: method name
        underTest.login("user1", "pass1")

        // Reset for second attempt
        fakeUserRepository.resetTrackingFlags()
        fakeUserRepository.configureLoginResult(ApiResult.Success(  // Fixed: method name
            LoginResponse("Login successful", "user123", "user2")
        ))
        fakeUserRepository.configureUserResult(ApiResult.Success(  // Fixed: method name
            UserResponse("user123", "user2", 5, 4.2)
        ))

        // Act
        underTest.login("user2", "pass2")

        // Assert
        val isLoggedIn = underTest.isLoggedIn.getOrAwaitValue()
        val errorMessage = underTest.errorMessage.getOrAwaitValue()

        assertTrue("Should be logged in on second attempt", isLoggedIn)
        assertNull("Previous error should be cleared", errorMessage)
    }
}