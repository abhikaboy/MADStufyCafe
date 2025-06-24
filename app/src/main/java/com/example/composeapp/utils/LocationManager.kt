package com.example.composeapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

data class UserLocation(
    val latitude: Double,
    val longitude: Double
)

class LocationHelper(private val context: Context) : LocationHelperInterface {
    
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    
    override fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    override suspend fun getCurrentLocation(): UserLocation? {

        if (!hasLocationPermission()) {
            return null
        }

        return try {
            suspendCancellableCoroutine { continuation ->
                try {
                    val lastKnownLocation = getLastKnownLocation()
                    if (lastKnownLocation != null) {
                        continuation.resume(UserLocation(lastKnownLocation.latitude, lastKnownLocation.longitude))
                        return@suspendCancellableCoroutine
                    }
                    
                    continuation.resume(null)
                } catch (e: SecurityException) {
                    continuation.resume(null)
                }
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getLastKnownLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }
        
        return try {
            val providers = locationManager.getProviders(true)
            var bestLocation: Location? = null
            
            for (provider in providers) {
                val location = locationManager.getLastKnownLocation(provider)
                if (location != null) {
                    if (bestLocation == null || location.accuracy < bestLocation.accuracy) {
                        bestLocation = location
                    }
                }
            }
            bestLocation
        } catch (e: SecurityException) {
            null
        }
    }
    
    companion object {
        // Default location (Boston, MA) as fallback
        val DEFAULT_LOCATION = UserLocation(
            latitude = 42.3601,
            longitude = -71.0589
        )
    }
} 