package com.skateshare.viewmodels

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.skateshare.R
import com.skateshare.db.LocalRoutesDao
import com.skateshare.misc.ExceptionResponse
import com.skateshare.models.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailedRouteViewModel @Inject constructor (
    var dao: LocalRoutesDao
    ): ViewModel() {

    private val _routeExceptionResponse = MutableLiveData<ExceptionResponse>()
    val routeExceptionResponse: LiveData<ExceptionResponse> get() = _routeExceptionResponse
    private val _routeData = MutableLiveData<Route>()
    val routeData: LiveData<Route> get() = _routeData

    fun getRoute(routeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _routeData.postValue(dao.getRouteById(routeId))
            } catch (e: Exception) {
                _routeExceptionResponse.postValue(
                    ExceptionResponse(e.message, false))
            }
        }
    }

    fun getSpeedData(): LineDataSet {
        val entries = mutableListOf<Entry>()
        val route = routeData.value

        route?.let {
            val speeds = it.speed
            for (i in speeds.indices)
                entries.add(Entry(i.toFloat(), speeds[i]))
        }

        return LineDataSet(entries, "Route speed")
    }

    fun resetResponse() {
        _routeExceptionResponse.postValue(
            ExceptionResponse(null, true))
    }
}