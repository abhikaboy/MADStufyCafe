package com.example.composeapp.utils.fakes

import com.example.composeapp.utils.LocationHelperInterface
import com.example.composeapp.utils.UserLocation

class FakeLocationHelper : LocationHelperInterface {
    private var currentLocation: UserLocation? = null
    private var hasPermission = true

    fun setCurrentLocation(location: UserLocation?) {
        currentLocation = location
    }

    fun setHasPermission(hasPermission: Boolean) {
        this.hasPermission = hasPermission
    }

    override suspend fun getCurrentLocation(): UserLocation? {
        return if (hasPermission) currentLocation else null
    }

    override fun hasLocationPermission(): Boolean {
        return hasPermission
    }

    companion object {
        val DEFAULT_LOCATION = UserLocation(
            latitude = 42.3601,
            longitude = -71.0589
        )
    }
}