package com.skateshare.viewmodels

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
                val snapshot = FirestorePost.getPost(postId)
                _routeData.postValue(snapshot.toRoutePost(hashMapOf()))      // No hashmap cache
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