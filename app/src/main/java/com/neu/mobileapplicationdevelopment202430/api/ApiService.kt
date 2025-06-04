package com.neu.mobileapplicationdevelopment202430.api

import com.neu.mobileapplicationdevelopment202430.model.Product
import retrofit2.http.GET

interface ApiService {
    @GET("getProducts")
    suspend fun getProducts(): List<Product>
} 