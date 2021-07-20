package com.skateshare.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.models.User
import com.skateshare.repostitories.FirestoreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModelFactory(private val profileUid: String?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java))
            return ProfileViewModel(profileUid) as T
        throw IllegalArgumentException("Unknown view model class!")
    }
}

class ProfileViewModel(private var profileUid: String?) : ViewModel() {
    private val currentUserUid = FirebaseAuth.getInstance().uid
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user
    var profileUserIsCurrentUser = false

    init {
        if (profileUid == null || profileUid == currentUserUid) {
            profileUid = currentUserUid
            profileUserIsCurrentUser = true
        }

        viewModelScope.launch(Dispatchers.IO) {
            _user.postValue(FirestoreService.getUserData(profileUid!!))
        }
    }
}