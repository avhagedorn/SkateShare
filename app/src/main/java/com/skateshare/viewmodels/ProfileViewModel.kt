package com.skateshare.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.modelUtils.toBoard
import com.skateshare.models.Board
import com.skateshare.models.LoadingItem
import com.skateshare.models.User
import com.skateshare.repostitories.FirestoreBoards
import com.skateshare.repostitories.FirestorePost
import com.skateshare.repostitories.FirestoreUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(private var profileUid: String?) : FeedViewModel() {
    private val currentUserUid = FirebaseAuth.getInstance().uid
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user
    private val _board = MutableLiveData<Board?>()
    val board: LiveData<Board?> get() = _board
    var profileUserIsCurrentUser = false

    init {
        if (profileUid.isNullOrEmpty() || profileUid == currentUserUid) {
            profileUid = currentUserUid
            profileUserIsCurrentUser = true
        }

        viewModelScope.launch(Dispatchers.IO) {
            _user.postValue(FirestoreUser.getUserData(profileUid!!))
        }
    }

    override fun fetchPosts() {
        isLoadingData = true
        viewModelScope.launch(Dispatchers.IO) {
            val newItems = queryToList(query = FirestorePost.getUserPosts(profileUid!!, end))

            if (newItems.isEmpty() && totalItems.last() is LoadingItem)
                totalItems.removeLastOrNull()

            if (newItems.isNotEmpty()) {
                totalItems.removeLastOrNull()
                end = newItems[newItems.size - 1].datePosted
                totalItems.addAll(newItems)
                totalItems.add(LoadingItem())
            }
            _numNewPosts.postValue(newItems.size)
            isLoadingData = false
        }
    }

    fun getBoard() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _board.postValue(FirestoreBoards.getBoard(profileUid!!).toBoard())
            } catch (e: Exception) {
                Log.i("1one", e.toString())
            }
        }
    }
}