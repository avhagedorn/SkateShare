package com.skateshare.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skateshare.db.LocalRoutesDao
import com.skateshare.misc.*
import com.skateshare.models.LoadingItem
import com.skateshare.models.Route
import com.skateshare.models.SimpleFeedItem
import com.skateshare.models.SimpleLoadingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PrivateRoutesViewModel @Inject constructor(
    val dao: LocalRoutesDao
) : ViewModel() {

    var isLoadingData = false
    private val allItems = mutableListOf<SimpleFeedItem>()
    private val _numNewRoutes = MutableLiveData<Int>()
    val numNewRoutes: LiveData<Int> get() = _numNewRoutes
    private val _deleteResponse = MutableLiveData<ExceptionResponse>()
    val deleteResponse: LiveData<ExceptionResponse> get() = _deleteResponse
    private var currentQueryAttribute = BY_DATE
    private var queryOffset = 0

    fun getRoutes() {
        isLoadingData = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newItems = when (currentQueryAttribute) {
                    BY_DATE -> dao.routesByDate(QUERY_LIMIT, queryOffset)
                    BY_DISTANCE -> dao.routesByDistance(QUERY_LIMIT, queryOffset)
                    BY_DURATION -> dao.routesByDuration(QUERY_LIMIT, queryOffset)
                    BY_SPEED -> dao.routesBySpeed(QUERY_LIMIT, queryOffset)
                    else -> throw Exception("Invalid query code!")
                }
                if (newItems.isEmpty()) {
                    if(allItems.isNotEmpty() && allItems.last() is SimpleLoadingItem)
                        allItems.removeLastOrNull()
                    _numNewRoutes.postValue(newItems.size)
                    isLoadingData = false
                } else {
                    allItems.removeLastOrNull()
                    queryOffset += newItems.size
                    allItems.addAll(newItems)
                    if (newItems.size == QUERY_LIMIT)
                        allItems.add(SimpleLoadingItem())
                    _numNewRoutes.postValue(newItems.size)
                    isLoadingData = false
                }
            } catch (e: Exception) {
                isLoadingData = false
                resetNumNewRoutes()
            }
        }
    }

    fun deleteRoute(index: Int, route: Route) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dao.delete(route)
                allItems.removeAt(index)
                _deleteResponse.postValue(ExceptionResponse(
                    message = null,
                    isSuccessful = true))
            } catch (e: Exception) {
                _deleteResponse.postValue(ExceptionResponse(
                    e.message,
                    isSuccessful = false))
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
        allItems.clear()
    }

    fun resetNumNewRoutes() {
        _numNewRoutes.postValue(-1)
    }

    fun resetDeleteResponse() {
        _deleteResponse.postValue(ExceptionResponse(
            message = null,
            isSuccessful = false,
            isEnabled = true))
    }

    fun getData() = allItems.toMutableList()

}