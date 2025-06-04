package com.neu.mobileapplicationdevelopment202430.api

import com.google.gson.GsonBuilder
import com.neu.mobileapplicationdevelopment202430.model.Product
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://mobileapplicationdevelopment.pythonanywhere.com/api/"
    
    private val gson = GsonBuilder()
        .registerTypeAdapter(Product::class.java, ProductTypeAdapter())
        .create()
    
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}