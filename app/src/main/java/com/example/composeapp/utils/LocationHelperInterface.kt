package com.example.composeapp.utils

interface LocationHelperInterface {
    suspend fun getCurrentLocation(): UserLocation?
    fun hasLocationPermission(): Boolean

    companion object {
        // Default location (Boston, MA) as fallback
        val DEFAULT_LOCATION = UserLocation(
            latitude = 42.3601,
            longitude = -71.0589
        )
    }
}
