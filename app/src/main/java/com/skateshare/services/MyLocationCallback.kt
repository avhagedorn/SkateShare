package com.skateshare.services

import android.location.Location
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.skateshare.misc.HAS_GPS
import com.skateshare.misc.MAX_RADIUS_METERS
import com.skateshare.misc.NO_GPS

class MyLocationCallback(): LocationCallback() {

    private lateinit var lastLocation: Location

    override fun onLocationResult(result: LocationResult) {
        super.onLocationResult(result)
        if (MapService.isTracking.value!!) {
            result.locations.let { locations ->
                for (location in locations) {
                    if (location.accuracy <= MAX_RADIUS_METERS) {
                        addDistance(location)
                        addLocation(location)
                        addSpeed(location.speed)
                    }
                }
            }
        }
    }

    override fun onLocationAvailability(p0: LocationAvailability) {
        super.onLocationAvailability(p0)
        MapService.warning.postValue(if (!p0.isLocationAvailable) NO_GPS else HAS_GPS)
    }

    private fun addLocation(location: Location?) {
        location?.let { loc ->
            val newLatLng = LatLng(loc.latitude, loc.longitude)
            MapService.routeData.value?.apply {
                add(newLatLng)
                MapService.routeData.postValue(this)
            }
        }
    }

    private fun addSpeed(speed: Float) {
        MapService.speedData.value?.apply {
            add(speed)
            MapService.speedData.postValue(this)
        }
    }

    private fun addDistance(currentLocation: Location) {
        if (MapService.routeData.value!!.isNotEmpty())
            MapService.distanceMeters.postValue(
                MapService.distanceMeters.value!! + currentLocation.distanceTo(lastLocation))
        lastLocation = currentLocation
    }
}