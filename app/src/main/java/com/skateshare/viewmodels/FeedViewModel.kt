package com.skateshare.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.skateshare.misc.POST_MEDIA
import com.skateshare.misc.POST_ROUTE
import com.skateshare.misc.RecyclerItemResponse
import com.skateshare.models.FeedItem
import com.skateshare.models.LoadingItem
import com.skateshare.models.Post
import com.skateshare.modelUtils.toPost
import com.skateshare.modelUtils.toRoutePost
import com.skateshare.repostitories.FirestorePost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class FeedViewModel : ViewModel() {

    private val currentUid = FirebaseAuth.getInstance().uid
    private var end: Timestamp = Timestamp.now()
    var isLoadingData: Boolean = false
    var totalItems = mutableListOf<FeedItem>()
    private val _numNewPosts = MutableLiveData<Int>()

    // Event responses
    val numNewPosts: LiveData<Int> get() = _numNewPosts
    private val userDataCache = HashMap<String, HashMap<String, String>>()
    private val _dbResponse = MutableLiveData<RecyclerItemResponse>()
    val dbResponse: LiveData<RecyclerItemResponse?> get() = _dbResponse

    init {
        fetchPosts()
    }

    fun fetchPosts() {
        isLoadingData = true
        viewModelScope.launch(Dispatchers.IO) {
            val newItems = queryToList(query = FirestorePost.getPosts(end))
            if (newItems.isNotEmpty()) {
                totalItems.addAll(newItems)
                end = newItems[newItems.size - 1].datePosted
            }
            _numNewPosts.postValue(newItems.size)
            isLoadingData = false
        }
    }

    private suspend fun queryToList(query: QuerySnapshot) : MutableList<FeedItem> {
        val queryResponse = mutableListOf<FeedItem>()
        query.forEach { item ->
            val post = when (item.getLong("postType")?.toInt()) {
                POST_MEDIA -> item.toPost(userDataCache)
                POST_ROUTE -> {
                    Log.i("1one", "ROUTE FOUND")
                    item.toRoutePost(userDataCache)
                }
                else -> item.toPost(userDataCache)
            }
            if (post != null) {
                queryResponse.add(post)
                post.isCurrentUser = currentUid == post.posterId
            }
        }
        queryResponse.reverse()
        return queryResponse
    }

    fun deletePost(id: String, position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                FirestorePost.deletePost(id)
                totalItems.removeAt(position)
                _dbResponse.postValue(RecyclerItemResponse(position, null, true))
            } catch (e: Exception) {
                _dbResponse.postValue(RecyclerItemResponse(-1, e.message, true))
            }
        }
    }

    fun resetRecyclerItemResponse() {
        _dbResponse.value = RecyclerItemResponse(-1, null, false)
    }

    fun refreshData() {
        end = Timestamp.now()
        totalItems.clear()
        fetchPosts()
    }

    fun getData() = totalItems.toMutableList<FeedItem>()

    fun getLoading() : MutableList<FeedItem> {
        val temp = getData()
        temp.add(LoadingItem())
        return temp
    }
}