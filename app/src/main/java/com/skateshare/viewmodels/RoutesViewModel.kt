package com.skateshare.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.skateshare.misc.ExceptionResponse
import com.skateshare.models.Route
import com.skateshare.repostitories.FirestoreRoutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoutesViewModel : ViewModel() {

    private val _firebaseResponse = MutableLiveData<ExceptionResponse>()
    val firebaseResponse: LiveData<ExceptionResponse> get() = _firebaseResponse

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