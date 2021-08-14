package com.skateshare.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skateshare.misc.ExceptionResponse
import com.skateshare.modelUtils.toRoutePost
import com.skateshare.models.RoutePost
import com.skateshare.repostitories.FirestorePost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PublicDetailedRouteViewModel : ViewModel() {

    private val _routeData = MutableLiveData<RoutePost>()
    val routeData: LiveData<RoutePost> get() = _routeData

    private val _firebaseResponse = MutableLiveData<ExceptionResponse>()
    val firebaseResponse: LiveData<ExceptionResponse> get() = _firebaseResponse

    fun getRoutePost(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.i("1one", postId)
                val snapshot = FirestorePost.getPost(postId)
                Log.i("1one", "snapshot -> ${snapshot.getString("id")}")
                val route = snapshot.toRoutePost(hashMapOf())
                if (route != null)
                    _routeData.postValue(route)      // No hashmap cache
                else
                    Log.i("1one", "route is null!")
            } catch (e: Exception) {
                _firebaseResponse.postValue(ExceptionResponse(
                    e.message,
                    isSuccessful = false))
            }
        }
    }

    fun resetResponse() {
        _firebaseResponse.postValue(ExceptionResponse(
            message = null,
            isSuccessful = false,
            isEnabled = false
        ))
    }

}