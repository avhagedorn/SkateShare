package com.skateshare.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.repostitories.FirestoreService
import com.skateshare.models.User
import com.squareup.okhttp.Dispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class ProfileViewModelFactory() : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java))
            return ProfileViewModel() as T
        throw IllegalArgumentException("Unknown view model class!")
    }
}

class ProfileViewModel : ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user
    val defaultProfilePicture = ""

    init {
        val uid = FirebaseAuth.getInstance().uid
        Log.d("ProfileViewModel", uid.toString())

        viewModelScope.launch(Dispatchers.IO) {
            _user.postValue(FirestoreService.getUserData(uid.toString()))
        }
    }
}