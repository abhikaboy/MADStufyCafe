package com.example.composeapp.data.network

import com.example.composeapp.data.network.ApiService
import com.example.composeapp.data.network.FakeApiService
import com.example.composeapp.data.network.UserLogin
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import kotlin.test.BeforeTest


class testFakeApi() {

    private lateinit var apiService: ApiService
    @Before
    fun setup() {
        apiService = FakeApiService()
    }

    @Test
    fun testFakeApiLogin() = runBlocking{
        val loginResponse = apiService.login(UserLogin("Tom", "123"))

        print(loginResponse)

    }
}