package com.skateshare.viewmodels.profile

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.modelUtils.toBoard
import com.skateshare.modelUtils.toUser
import com.skateshare.models.Board
import com.skateshare.models.LoadingItem
import com.skateshare.models.User
import com.skateshare.repostitories.FirestoreBoards
import com.skateshare.repostitories.FirestorePost
import com.skateshare.repostitories.FirestoreUser
import com.skateshare.viewmodels.feed.FeedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModelFactory(private val profileUid: String?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java))
            return ProfileViewModel(profileUid) as T
        throw IllegalArgumentException("Unknown view model class!")
    }
}

class ProfileViewModel(private var profileUid: String?) : FeedViewModel() {
    private val currentUserUid = FirebaseAuth.getInstance().uid
    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user
    private val _board = MutableLiveData<Board?>()
    val board: LiveData<Board?> get() = _board
    var profileUserIsCurrentUser = false

    init {
        if (profileUid.isNullOrEmpty() || profileUid == currentUserUid) {
            profileUid = currentUserUid
            profileUserIsCurrentUser = true
        }

        viewModelScope.launch(Dispatchers.IO) {
            _user.postValue(FirestoreUser.getUserData(profileUid!!).toUser())
        }
    }

    override fun fetchPosts() {
        isLoadingData = true
        viewModelScope.launch(Dispatchers.IO) {
            val newItems = queryToList(query = FirestorePost.getUserPosts(profileUid!!, end))

            if (newItems.isEmpty()) {
                if (totalItems.isNotEmpty() && totalItems.last() is LoadingItem)
                    totalItems.removeLastOrNull()
                _numNewPosts.postValue(newItems.size)
                isLoadingData = false
            }
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
                // TODO
            }
        }
    }
}