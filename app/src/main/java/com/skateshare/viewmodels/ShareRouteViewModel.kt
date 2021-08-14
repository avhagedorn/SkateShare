package com.skateshare.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.skateshare.db.LocalRoutesDao
import com.skateshare.misc.EventResponse
import com.skateshare.misc.ExceptionResponse
import com.skateshare.models.Route
import com.skateshare.repostitories.FirestoreRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareRouteViewModel @Inject constructor (
    var dao: LocalRoutesDao
): ViewModel() {

    private val _postResponse = MutableLiveData<ExceptionResponse>()
    val postResponse: LiveData<ExceptionResponse> get() = _postResponse

    fun submitPost(routeId: Long, routeDescription: String, boardType: String,
                   terrainType: String, roadType: String) {

        if (dataIsValid(
                routeDescription,
                listOf(boardType, terrainType, roadType))) {

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val route = dao.getRouteById(routeId)!!
                    FirestoreRoutes.createRoute(
                        route = route,
                        description = routeDescription,
                        boardType = boardType,
                        terrainType = terrainType,
                        roadType = roadType,
                        path = encodePolylinePath(route)
                    )
                    _postResponse.postValue(ExceptionResponse(
                            message = null,
                            isSuccessful = true))
                } catch (e: Exception) {
                    _postResponse.postValue(ExceptionResponse(
                        e.message,
                        isSuccessful = false))
                }
            }
        }
    }

    fun resetResponse() {
        _postResponse.postValue(ExceptionResponse(
            message = null,
            isSuccessful = false,
            isEnabled = false))
    }

    private fun dataIsValid(description: String, postChoices: List<String>): Boolean {

        if (description.isEmpty()) {
            _postResponse.postValue(
                ExceptionResponse("Description is empty!", false)
            )
            return false
        }

        postChoices.forEach { choice ->
            if (choice == "null") {
                _postResponse.postValue(
                    ExceptionResponse("Please fill out all fields!", false)
                )
                return false
            }
        }
        return true
    }

    private fun encodePolylinePath(route: Route) : String {
        val latLngPath = mutableListOf<LatLng>()
        val lats = route.lat_path
        val lngs = route.lng_path

        for (i in lats.indices) {
            latLngPath.add(LatLng(lats[i], lngs[i]))
        }

        return PolyUtil.encode(latLngPath)
    }

}