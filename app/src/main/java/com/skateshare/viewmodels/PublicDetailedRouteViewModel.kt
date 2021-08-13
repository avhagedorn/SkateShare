package com.skateshare.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skateshare.modelUtils.toRoutePost
import com.skateshare.models.RoutePost
import com.skateshare.repostitories.FirestorePost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PublicDetailedRouteViewModel : ViewModel() {

    private val _routeData = MutableLiveData<RoutePost>()
    val routeData: LiveData<RoutePost> get() = _routeData

    fun getRoutePost(postId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val snapshot = FirestorePost.getPost(postId)
                _routeData.postValue(snapshot.toRoutePost(hashMapOf()))           // Dummy hashmap cache
            } catch (e: Exception) {
                // send error response
            }
        }
    }

}