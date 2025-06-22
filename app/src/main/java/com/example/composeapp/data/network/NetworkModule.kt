package com.example.composeapp.data.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    
    // Update this with your actual API base URL
    private const val BASE_URL = "https://study-cafe-api-p3evt.ondigitalocean.app/"
    
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
}

// Extension function to safely handle API calls
suspend fun <T> safeApiCall(apiCall: suspend () -> T): ApiResult<T> {
    return try {
        ApiResult.Success(apiCall())
    } catch (throwable: Throwable) {
        ApiResult.Error(
            when (throwable) {
                is retrofit2.HttpException -> {
                    when (throwable.code()) {
                        401 -> "Unauthorized access"
                        404 -> "Resource not found"
                        422 -> "Validation error"
                        500 -> "Server error"
                        else -> "HTTP ${throwable.code()}: ${throwable.message()}"
                    }
                }
                is java.net.SocketTimeoutException -> "Request timeout"
                is java.net.UnknownHostException -> "No internet connection"
                else -> throwable.localizedMessage ?: "Unknown error"
            },
            throwable
        )
    }
}

// Result wrapper for API calls
sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val message: String, val exception: Throwable? = null) : ApiResult<Nothing>()
    
    val isSuccess: Boolean
        get() = this is Success
    
    val isError: Boolean
        get() = this is Error
    
    fun onSuccess(action: (T) -> Unit): ApiResult<T> {
        if (this is Success) {
            action(data)
        }
        return this
    }
    
    fun onError(action: (String, Throwable?) -> Unit): ApiResult<T> {
        if (this is Error) {
            action(message, exception)
        }
        return this
    }
} 