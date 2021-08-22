package com.skateshare.viewmodels.routes

import androidx.lifecycle.*
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.skateshare.db.LocalRoutesDao
import com.skateshare.misc.*
import com.skateshare.models.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailedRouteViewModel @Inject constructor (
    var dao: LocalRoutesDao
    ): ViewModel() {

    private val _routeResponse = MutableLiveData<ExceptionResponse>()
    val routeResponse: LiveData<ExceptionResponse> get() = _routeResponse
    private val _routeData = MutableLiveData<Route>()
    val routeData: LiveData<Route> get() = _routeData

    fun getRoute(routeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _routeData.postValue(dao.getRouteById(routeId))
            } catch (e: Exception) {
                _routeResponse.postValue(ExceptionResponse(
                    e.message,
                    isSuccessful = false))
            }
        }
    }

    fun getSpeedData(unit: String): LineDataSet {
        val multiplier = when (unit) {
            UNIT_MILES -> METERS_SEC_TO_MI_HR
            UNIT_KILOMETERS -> METERS_SEC_TO_KM_HR
            else -> 0f
        }
        val entries = mutableListOf<Entry>()
        val route = routeData.value

        route?.let {
            val speeds = it.speed
            for (i in speeds.indices) {
                val speed = speeds[i]*multiplier
                entries.add(Entry(i.toFloat(), speed))
            }
        }

        return LineDataSet(entries, "Route speed")
    }

    fun resetResponse() {
        _routeResponse.postValue(
            ExceptionResponse(
                message = null,
                isSuccessful = false,
                isEnabled = false))
    }
}