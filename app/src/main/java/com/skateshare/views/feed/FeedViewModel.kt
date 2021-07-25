package com.skateshare.views.feed

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
            try {
                _posts.postValue(DummyPostRepository.getPosts())
            } catch(e: Exception) {
                _posts.postValue(listOf())
            }
        }
    }
}