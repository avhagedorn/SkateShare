package com.skateshare.views.Feed

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skateshare.models.Post
import com.skateshare.repostitories.DummyPostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FeedViewModel : ViewModel() {

    private val _posts = MutableLiveData<List<Post?>>()
    val posts: LiveData<List<Post?>> get() = _posts

    init {
        initializePosts()
    }

    private fun initializePosts() {
        viewModelScope.launch(Dispatchers.IO) {
            _posts.postValue(DummyPostRepository.getPosts())
        }
    }
}