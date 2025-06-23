package com.example.composeapp.utils

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.composeapp.data.database.AppDatabase
import com.example.composeapp.data.network.ApiResult
import com.example.composeapp.data.network.NetworkModule
import com.example.composeapp.data.repository.CafeRepository

class CafeWorker(
    appContext: Context,
    workerParams: WorkerParameters,
): CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val cafeDao = database.cafeDao()
        val apiService = NetworkModule.apiService
        val locationHelper = LocationHelper(applicationContext)
        val cafeRepository = CafeRepository(cafeDao, apiService, locationHelper)
        val result = cafeRepository.refreshCafes()
        when (result) {
            is ApiResult.Success -> {
                Log.d("CafeWorker", "Successfully refreshed ${result.data.size} cafes")
            }
            is ApiResult.Error -> {
                Log.d("CafeWorker", "Failed to refresh cafes: ${result.message}")
            }
        }
        Log.d("CafeWorker", "Worker completed")
        return Result.success()
    }
}