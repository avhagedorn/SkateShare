package com.skateshare.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skateshare.models.RoutePost
import com.skateshare.repostitories.FirestoreRoutePosts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PublicRoutesViewModel : ViewModel() {

    var routes: List<RoutePost> = listOf()
    private val _hasData = MutableLiveData<Boolean>()
    val hasData: LiveData<Boolean> get() = _hasData

    fun getData(lat: Double, lng: Double, radius: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val posts =
                    FirestoreRoutePosts.getRoutePostsAboutRadius(
                        lat,
                        lng,
                        radius
                    )
                routes = sortList(posts)
                _hasData.postValue(true)
            } catch (e: Exception) {
                Log.i("1one", e.message.toString())
            }
        }
    }

    private fun sortList(routeList: List<RoutePost>) =
        routeList.sortedBy { it.distanceToCenter }
}