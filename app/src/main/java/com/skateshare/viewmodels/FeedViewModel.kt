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
    val postsTotal = mutableListOf<Post?>(null)
    private val _numNewPosts = MutableLiveData<Int>()
    val numNewPosts: LiveData<Int> get() = _numNewPosts

    init {
        fetchPosts()
    }

    fun fetchPosts() {
        isLoadingData = true
        viewModelScope.launch(Dispatchers.IO) {
            val newPosts = queryToList(query = DummyPostRepository.getPosts(end))
            postsTotal.removeLast() // Remove null (removes loading icon)
            _numNewPosts.postValue(newPosts.size)
            if (newPosts.isNotEmpty()) {
                postsTotal.addAll(newPosts)
                end = newPosts[newPosts.size - 1].datePosted
            }
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
}