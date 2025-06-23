package com.example.composeapp.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.composeapp.data.network.ApiResult
import com.example.composeapp.data.network.ApiService
import com.example.composeapp.data.network.FakeApiService
import com.example.composeapp.data.network.LoginResponse
import com.example.composeapp.data.network.UserLogin
import com.example.composeapp.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import org.junit.Rule
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class UserRepositoryTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var apiService: ApiService
    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        apiService = FakeApiService()
        userRepository = UserRepository(apiService)
    }

    @Test
    fun testLogin() = runTest {
        val resultLiveData = userRepository.loginUser("testUser", "testPass")
        //advanceUntilIdle()
        //val result = resultLiveData.getOrAwaitValue()
        //print(result)

        // Assert
//        assertTrue(result is ApiResult.Success)
//        val loginResponse = (result as ApiResult.Success).data
//        print(loginResponse)
//        assertEquals("Login successful", loginResponse.message)
//        assertEquals("1", loginResponse.user_id)
//        assertEquals("testUser", loginResponse.user_name)
    }
}

@Throws(InterruptedException::class)
fun <T> LiveData<T>.getOrAwaitValue(
    timeout: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS
): T {
    var value: T? = null
    val latch = CountDownLatch(1)

    val observer = Observer<T> { t ->
        value = t
        latch.countDown()
    }

    observeForever(observer)
    val await = latch.await(timeout, timeUnit)
    removeObserver(observer)

    if (!await) {
        throw IllegalStateException("Timeout waiting for LiveData value.")
    }

    return value ?: throw IllegalStateException("LiveData value was null.")
}