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
        android.util.Log.d("LocationHelper", "getCurrentLocation() called")
        
        if (!hasLocationPermission()) {
            android.util.Log.d("LocationHelper", "No location permission granted")
            return null
        }
        
        android.util.Log.d("LocationHelper", "Location permission granted, trying to get location")
        
        return try {
            suspendCancellableCoroutine { continuation ->
                try {
                    // Try to get last known location first (faster)
                    val lastKnownLocation = getLastKnownLocation()
                    if (lastKnownLocation != null) {
                        android.util.Log.d("LocationHelper", "Found last known location: lat=${lastKnownLocation.latitude}, lng=${lastKnownLocation.longitude}")
                        continuation.resume(UserLocation(lastKnownLocation.latitude, lastKnownLocation.longitude))
                        return@suspendCancellableCoroutine
                    }
                    
                    // If no last known location, return null (we could implement active location requests here)
                    android.util.Log.d("LocationHelper", "No last known location available")
                    continuation.resume(null)
                } catch (e: SecurityException) {
                    android.util.Log.e("LocationHelper", "SecurityException: ${e.message}")
                    continuation.resume(null)
                }
            }
        } catch (e: Exception) {
            android.util.Log.e("LocationHelper", "Exception getting location: ${e.message}")
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