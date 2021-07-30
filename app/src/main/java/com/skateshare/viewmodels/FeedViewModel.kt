package com.skateshare.viewmodels

import com.google.type.Date
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.QuerySnapshot
import com.skateshare.models.Post
import com.skateshare.models.Post.Companion.toPost
import com.skateshare.repostitories.DummyPostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class FeedViewModel : ViewModel() {

    private val currentUid = FirebaseAuth.getInstance().uid
    private var end: Timestamp = Timestamp.now()
    var isLoadingData: Boolean = false
    var totalPosts = mutableListOf<Post>()
    private val _numNewPosts = MutableLiveData<EventResponse>()
    val numNewPosts: LiveData<EventResponse> get() = _numNewPosts
    val userDataCache = HashMap<String, List<String>>()

    // Event responses
    private val _dbResponse = MutableLiveData<RecyclerItemResponse>()
    val dbResponse: LiveData<RecyclerItemResponse?> get() = _dbResponse

    init {
        fetchPosts()
    }

    fun fetchPosts() {
        isLoadingData = true
        viewModelScope.launch(Dispatchers.IO) {
            val newPosts = queryToList(query = DummyPostRepository.getPosts(end))
            if (newPosts.isNotEmpty()) {
                totalPosts.addAll(newPosts)
                end = newPosts[newPosts.size - 1].datePosted
            }
            _numNewPosts.postValue(EventResponse(newPosts.size, true))
            isLoadingData = false
        }
    }

    private suspend fun queryToList(query: QuerySnapshot) : MutableList<Post> {
        val queryResponse = mutableListOf<Post>()
        query.forEach { item ->
            val post = item.toPost()
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
                DummyPostRepository.deletePost(id)
                totalPosts.removeAt(position)
                _dbResponse.postValue(RecyclerItemResponse(position, null, true))
            } catch (e: Exception) {
                _dbResponse.postValue(RecyclerItemResponse(-1, e.message, true))
            }
        }
    }

    fun resetRecyclerItemResponse() {
        _dbResponse.value = RecyclerItemResponse(-1, null, false)
    }

    fun resetPostRequest() {
        _numNewPosts.value = EventResponse(-1, false)
    }

    fun refreshData() {
        end = Timestamp.now()
        totalPosts.clear()
        fetchPosts()
    }

    fun getData() = totalPosts.toMutableList()
}