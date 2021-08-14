package com.skateshare.viewmodels

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.models.User
import com.skateshare.repostitories.FirestoreUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileViewModel(private var profileUid: String?) : ViewModel() {
    private val currentUserUid = FirebaseAuth.getInstance().uid
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user
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
}