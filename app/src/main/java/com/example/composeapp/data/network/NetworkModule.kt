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
    
    // Custom interceptor for detailed API logging
    private val apiLoggingInterceptor = Interceptor { chain ->
        val request = chain.request()
        val startTime = System.currentTimeMillis()
        
        // Log request details
        Log.d(TAG, "╔══════════════════════════════════════════════════════════════")
        Log.d(TAG, "║ 🚀 API REQUEST")
        Log.d(TAG, "║ Method: ${request.method}")
        Log.d(TAG, "║ URL: ${request.url}")
        Log.d(TAG, "║ Headers: ${request.headers}")
        request.body?.let { body ->
            Log.d(TAG, "║ Body Content-Type: ${body.contentType()}")
            Log.d(TAG, "║ Body Size: ${body.contentLength()} bytes")
        }
        Log.d(TAG, "║ Time: ${java.text.SimpleDateFormat("HH:mm:ss.SSS", java.util.Locale.getDefault()).format(java.util.Date())}")
        Log.d(TAG, "╠══════════════════════════════════════════════════════════════")
        
        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            val endTime = System.currentTimeMillis()
            Log.e(TAG, "║ ❌ API REQUEST FAILED")
            Log.e(TAG, "║ URL: ${request.url}")
            Log.e(TAG, "║ Error: ${e.message}")
            Log.e(TAG, "║ Duration: ${endTime - startTime}ms")
            Log.e(TAG, "╚══════════════════════════════════════════════════════════════")
            throw e
        }
        
        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime
        
        // Log response details
        Log.d(TAG, "║ ✅ API RESPONSE")
        Log.d(TAG, "║ Status: ${response.code} ${response.message}")
        Log.d(TAG, "║ URL: ${request.url}")
        Log.d(TAG, "║ Duration: ${duration}ms")
        Log.d(TAG, "║ Response Headers: ${response.headers}")
        Log.d(TAG, "║ Content-Type: ${response.body?.contentType()}")
        Log.d(TAG, "║ Content-Length: ${response.body?.contentLength()} bytes")
        
        if (response.isSuccessful) {
            Log.d(TAG, "║ Result: SUCCESS ✅")
        } else {
            Log.w(TAG, "║ Result: ERROR ❌")
        }
        
        Log.d(TAG, "╚══════════════════════════════════════════════════════════════")
        
        response
    }
    
    // Standard HTTP logging interceptor for body content
    private val httpLoggingInterceptor = HttpLoggingInterceptor { message ->
        Log.d("$TAG-BODY", message)
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(apiLoggingInterceptor) // Our custom interceptor first
        .addInterceptor(httpLoggingInterceptor) // Then the standard one for body logging
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

// Enhanced extension function with detailed logging
suspend fun <T> safeApiCall(
    apiName: String = "Unknown API",
    apiCall: suspend () -> T
): ApiResult<T> {
    val startTime = System.currentTimeMillis()
    Log.d("API_CALL", "🔄 Starting API call: $apiName")
    
    return try {
        val result = apiCall()
        val duration = System.currentTimeMillis() - startTime
        Log.d("API_CALL", "✅ API call successful: $apiName (${duration}ms)")
        ApiResult.Success(result)
    } catch (throwable: Throwable) {
        val duration = System.currentTimeMillis() - startTime
        val errorMessage = when (throwable) {
            is retrofit2.HttpException -> {
                val errorBody = throwable.response()?.errorBody()?.string()
                Log.e("API_CALL", "❌ HTTP Error in $apiName:")
                Log.e("API_CALL", "   Status: ${throwable.code()}")
                Log.e("API_CALL", "   Message: ${throwable.message()}")
                Log.e("API_CALL", "   Error Body: $errorBody")
                Log.e("API_CALL", "   Duration: ${duration}ms")
                
                when (throwable.code()) {
                    401 -> "Unauthorized access - Please check your credentials"
                    404 -> "Resource not found - The requested endpoint may not exist"
                    422 -> "Validation error - Please check your request data"
                    500 -> "Server error - Please try again later"
                    else -> "HTTP ${throwable.code()}: ${throwable.message()}"
                }
            }
            is java.net.SocketTimeoutException -> {
                Log.e("API_CALL", "❌ Timeout in $apiName after ${duration}ms")
                "Request timeout - Please check your internet connection"
            }
            is java.net.UnknownHostException -> {
                Log.e("API_CALL", "❌ Network error in $apiName: ${throwable.message}")
                "No internet connection - Please check your network"
            }
            is java.net.ConnectException -> {
                Log.e("API_CALL", "❌ Connection error in $apiName: ${throwable.message}")
                "Unable to connect to server - Please try again"
            }
            else -> {
                Log.e("API_CALL", "❌ Unknown error in $apiName: ${throwable.message}")
                throwable.localizedMessage ?: "Unknown error occurred"
            }
        }
        
        ApiResult.Error(errorMessage, throwable)
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