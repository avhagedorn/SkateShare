package com.skateshare.viewmodels

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
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

class ProfileViewModelFactory(private val profileUid: String?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java))
            return ProfileViewModel(profileUid) as T
        throw IllegalArgumentException("Unknown view model class!")
    }
}

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
        _routeResponse.postValue(
            ExceptionResponse(
                message = null,
                isSuccessful = false,
                isEnabled = false))
    }
}