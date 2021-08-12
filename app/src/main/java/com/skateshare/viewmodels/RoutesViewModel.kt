package com.skateshare.viewmodels

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.skateshare.misc.DEFAULT_LOCATION
import com.skateshare.misc.ExceptionResponse
import com.skateshare.misc.MAX_ZOOM_RADIUS
import com.skateshare.misc.MIN_ZOOM_QUERY
import com.skateshare.models.ReverseGeocodeLocation
import com.skateshare.models.Route
import com.skateshare.models.RouteGlobalMap
import com.skateshare.repostitories.FirestoreRoutes
import com.skateshare.repostitories.createReverseGeocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.pow

class RoutesViewModel : ViewModel() {

    private val _firebaseResponse = MutableLiveData<ExceptionResponse>()
    val firebaseResponse: LiveData<ExceptionResponse> get() = _firebaseResponse
    private val _publicRoutes = MutableLiveData<List<RouteGlobalMap>>()
    val publicRoutes: LiveData<List<RouteGlobalMap>> get() = _publicRoutes

    private var queryCenter = DEFAULT_LOCATION
    private var queryRadius = 0.0

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

    private fun geoQueryAbout(coordinate: LatLng, radius: Double) {
        val lat = coordinate.latitude
        val lng = coordinate.longitude
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _publicRoutes.postValue(
                    FirestoreRoutes.getRoutesAboutRadius(lat, lng, radius))
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
            }
        }
    }

    private fun calculateRadiusFromZoom(zoom: Float) = MAX_ZOOM_RADIUS / 2.0.pow(zoom + 4.0)

    fun resetResponse() {
        _firebaseResponse.postValue(ExceptionResponse(null, false))
    }
}