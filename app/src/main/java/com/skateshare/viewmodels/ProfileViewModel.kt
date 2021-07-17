package com.skateshare.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.repostitories.FirestoreService
import com.skateshare.models.User
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

    init {
        val uid = FirebaseAuth.getInstance().uid
        Log.d("ProfileViewModel", uid.toString())

        viewModelScope.launch {
            _user.value = FirestoreService.getUserData(uid.toString())
        }
    }
}