package com.skateshare.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skateshare.db.LocalRoutesDao
import com.skateshare.misc.*
import com.skateshare.models.Route
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class PrivateRoutesViewModel @Inject constructor(
    val dao: LocalRoutesDao
) : ViewModel() {

    var isLoadingData = false
    val allRoutes = mutableListOf<Route>()
    private var newRoutes = listOf<Route>()
    private val _hasNewRoutes = MutableLiveData<Boolean>()
    val hasNewRoutes: LiveData<Boolean> get() = _hasNewRoutes
    var currentQueryAttribute = BY_DATE
    private var queryOffset = 0

    init {
        getRoutes()
    }

    fun getRoutes() {
        isLoadingData = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                newRoutes = when (currentQueryAttribute) {
                    BY_DATE -> dao.routesByDate(QUERY_LIMIT, queryOffset)
                    BY_DISTANCE -> dao.routesByDistance(QUERY_LIMIT, queryOffset)
                    BY_DURATION -> dao.routesByDuration(QUERY_LIMIT, queryOffset)
                    BY_SPEED -> dao.routesBySpeed(QUERY_LIMIT, queryOffset)
                    else -> throw Exception("Invalid query code!")
                }
                queryOffset += newRoutes.size
                allRoutes.addAll(getCurrentRoutes())
                _hasNewRoutes.postValue(true)
                isLoadingData = false
            } catch (e: Exception) {
                Log.i("1one", e.toString())
                isLoadingData = false
            }
        }
    }

    fun updateSortingPreference(newPreference: Int) {
        val choices = listOf(BY_DATE, BY_DISTANCE, BY_DURATION, BY_SPEED)
        if (newPreference in choices)
            currentQueryAttribute = newPreference
        else
            throw Exception("Invalid query code!")
    }

    fun clearExistingRoutes() {
        queryOffset = 0
        allRoutes.clear()
        newRoutes = listOf()
    }

    fun resetHasNewRoutes() {
        _hasNewRoutes.postValue(false)
    }

    fun getTotalRoutes() = allRoutes.toMutableList()

    fun getCurrentRoutes() = newRoutes.toMutableList()

}