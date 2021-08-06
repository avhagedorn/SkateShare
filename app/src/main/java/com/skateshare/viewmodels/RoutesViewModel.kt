package com.skateshare.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.skateshare.misc.ExceptionResponse
import com.skateshare.models.Route
import com.skateshare.repostitories.FirestoreRoutes
import com.skateshare.services.MAX_ZOOM_RADIUS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Math.pow
import kotlin.math.pow

class RoutesViewModel : ViewModel() {

    private val _firebaseResponse = MutableLiveData<ExceptionResponse>()
    val firebaseResponse: LiveData<ExceptionResponse> get() = _firebaseResponse

    private val _publicRoutes = MutableLiveData<List<Route>>()
    val publicRoutes: LiveData<List<Route>> get() = _publicRoutes

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

    fun geoQueryAbout(coordinate: LatLng, zoom: Double) {
        val lat = coordinate.latitude
        val lng = coordinate.longitude
        val radius = calculateRadiusFromZoom(zoom)
        
        Log.i("1one", "Radius: $radius")

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _publicRoutes.postValue(FirestoreRoutes.getRoutesAboutRadius(lat, lng, radius))
            } catch (e: Exception) {
                Log.i("1one", e.message.toString())
            }
        }
    }

    private fun calculateRadiusFromZoom(zoom: Double) = MAX_ZOOM_RADIUS / 2.0.pow(zoom + 3)

    fun resetResponse() {
        _firebaseResponse.postValue(ExceptionResponse(null, false))
    }

    fun toLatLng(lats: List<Double>, lngs: List<Double>) : List<LatLng> {
        val coordinates = mutableListOf<LatLng>()
        for (i in lats.indices) {
            coordinates.add(LatLng(lats[i], lngs[i]))
        }
        return coordinates
    }
}