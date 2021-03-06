package com.skateshare.viewmodels.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.skateshare.misc.ExceptionResponse
import com.skateshare.misc.POST_MEDIA
import com.skateshare.misc.POST_ROUTE
import com.skateshare.misc.QUERY_LIMIT
import com.skateshare.modelUtils.toPost
import com.skateshare.modelUtils.toRoutePost
import com.skateshare.models.FeedItem
import com.skateshare.models.LoadingItem
import com.skateshare.repostitories.FirestorePost
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

open class FeedViewModel : ViewModel() {

    private val currentUid = FirebaseAuth.getInstance().uid
    var end: Timestamp = Timestamp.now()
    var isLoadingData: Boolean = false
    var totalItems = mutableListOf<FeedItem>()
    protected val _numNewPosts = MutableLiveData<Int>()

    // Event responses
    val numNewPosts: LiveData<Int> get() = _numNewPosts
    private val userDataCache = HashMap<String, HashMap<String, String>>()
    private val _deleteResponse = MutableLiveData<ExceptionResponse>()
    val deleteResponse: LiveData<ExceptionResponse> get() = _deleteResponse

    open fun fetchPosts() {
        isLoadingData = true
        viewModelScope.launch(Dispatchers.IO) {
            val newItems = queryToList(query = FirestorePost.getPosts(end))

            if (newItems.isEmpty()) {
                if (totalItems.isNotEmpty() && totalItems.last() is LoadingItem)
                    totalItems.removeLastOrNull()
                _numNewPosts.postValue(newItems.size)
                isLoadingData = false
            }
            else if (newItems.isNotEmpty()) {
                totalItems.removeLastOrNull()
                end = newItems[newItems.size - 1].datePosted
                totalItems.addAll(newItems)
                if (newItems.size == QUERY_LIMIT)
                    totalItems.add(LoadingItem())
                _numNewPosts.postValue(newItems.size)
                isLoadingData = false
            }
        }
    }

    suspend fun queryToList(query: QuerySnapshot) : MutableList<FeedItem> {
        val queryResponse = mutableListOf<FeedItem>()
        query.forEach { item ->
            val post = when (item.getLong("postType")?.toInt()) {
                POST_MEDIA -> item.toPost(userDataCache)
                POST_ROUTE -> item.toRoutePost(userDataCache)
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

    fun deletePost(id: String, type: Int, position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                FirestorePost.deletePost(id, type)
                totalItems.removeAt(position)
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

    fun resetRecyclerItemResponse() {
        _deleteResponse.value = ExceptionResponse(
            message = null,
            isSuccessful = false,
            isEnabled = false)
    }

    fun resetNumNewPosts() {
        _numNewPosts.postValue(-1)
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