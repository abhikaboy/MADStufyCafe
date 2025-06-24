package com.example.composeapp.data.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {
    
    private const val TAG = "API_LOGGER"
    private const val BASE_URL = "https://study-cafe-api-p3evt.ondigitalocean.app/"
    
    private val gson: Gson = GsonBuilder()
        .setLenient()
        .setPrettyPrinting()
        .create()
    
    private val apiLoggingInterceptor = Interceptor { chain ->
        val request = chain.request()
        val startTime = System.currentTimeMillis()
        
        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            throw e
        }
        
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        
        response
    }
    
    private val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("$TAG-BODY", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(apiLoggingInterceptor)
        .addInterceptor(httpLoggingInterceptor)
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

suspend fun <T> safeApiCall(
    apiName: String = "Unknown API",
    apiCall: suspend () -> T
): ApiResult<T> {
    val startTime = System.currentTimeMillis()
    return try {
        val result = apiCall()
        val duration = System.currentTimeMillis() - startTime
        ApiResult.Success(result)
    } catch (throwable: Throwable) {
        val duration = System.currentTimeMillis() - startTime
        val errorMessage = when (throwable) {
            is retrofit2.HttpException -> {
                val errorBody = throwable.response()?.errorBody()?.string()
                when (throwable.code()) {
                    401 -> "Unauthorized access - Please check your credentials"
                    404 -> "Resource not found - The requested endpoint may not exist"
                    422 -> "Validation error - Please check your request data"
                    500 -> "Server error - Please try again later"
                    else -> "HTTP ${throwable.code()}: ${throwable.message()}"
                }
            }
            is java.net.SocketTimeoutException -> {
                "Request timeout - Please check your internet connection"
            }
            is java.net.UnknownHostException -> {
                "No internet connection - Please check your network"
            }
            is java.net.ConnectException -> {
                "Unable to connect to server - Please try again"
            }
            else -> {
                throwable.localizedMessage ?: "Unknown error occurred"
            }
        }
        
        ApiResult.Error(errorMessage, throwable)
    }
}

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