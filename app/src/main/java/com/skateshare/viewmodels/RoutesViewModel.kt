package com.skateshare.viewmodels

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.skateshare.misc.ExceptionResponse
import com.skateshare.models.FirebaseRoute
import com.skateshare.models.Route
import com.skateshare.repostitories.FirestoreRoutes
import com.skateshare.services.DEFAULT_LOCATION
import com.skateshare.services.MAX_ZOOM_RADIUS
import com.skateshare.services.MIN_ZOOM_QUERY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Math.pow
import kotlin.math.pow

class RoutesViewModel : ViewModel() {

    private val _firebaseResponse = MutableLiveData<ExceptionResponse>()
    val firebaseResponse: LiveData<ExceptionResponse> get() = _firebaseResponse
    private val _publicRoutes = MutableLiveData<List<FirebaseRoute>>()
    val publicRoutes: LiveData<List<FirebaseRoute>> get() = _publicRoutes

    private var queryCenter = DEFAULT_LOCATION
    private var queryRadius = 0.0

    fun publishRouteToFirestore(route: Route) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                FirestoreRoutes.createRoute(route)
                _firebaseResponse.postValue(
                    ExceptionResponse("Uploaded route successfully!", true))
            } catch (e: Exception) {
                _firebaseResponse.postValue(
                    ExceptionResponse(e.message.toString(), false))
            }
        }
    }

    fun deleteRouteFromFirestore(routeId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                FirestoreRoutes.deleteRoute(routeId)
                _firebaseResponse.postValue(
                    ExceptionResponse("Removed route successfully!", true))
            } catch (e: Exception) {
                _firebaseResponse.postValue(
                    ExceptionResponse(e.message.toString(), false))
            }
        }
    }

    fun geoQueryAbout(coordinate: LatLng, radius: Double) {
        val lat = coordinate.latitude
        val lng = coordinate.longitude
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _publicRoutes.postValue(FirestoreRoutes.getRoutesAboutRadius(lat, lng, radius))
            } catch (e: Exception) {
                Log.i("1one12", e.message.toString())
            }
        }
    }

    fun geoQueryIfNeeded(currentCoordinate: LatLng, zoom: Float) {
        if (zoom >= MIN_ZOOM_QUERY) {
            val currentRadius = calculateRadiusFromZoom(zoom)
            val distanceFromQueryToCenter = FloatArray(1)
            Location.distanceBetween(
                queryCenter.latitude,
                queryCenter.longitude,
                currentCoordinate.latitude,
                currentCoordinate.longitude,
                distanceFromQueryToCenter
            )

            if (distanceFromQueryToCenter[0] + currentRadius > queryRadius) {
                geoQueryAbout(currentCoordinate, currentRadius*2)
                queryCenter = currentCoordinate
                queryRadius = currentRadius*2
                Log.i("1one", "PERFORMED QUERY")
            }
        }
    }

    fun calculateRadiusFromZoom(zoom: Float) = MAX_ZOOM_RADIUS / 2.0.pow(zoom + 4.0)

    fun resetResponse() {
        _firebaseResponse.postValue(ExceptionResponse(null, false))
    }
}