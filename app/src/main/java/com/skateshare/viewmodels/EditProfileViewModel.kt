package com.skateshare.viewmodels

import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.models.User
import com.skateshare.repostitories.FirestoreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel() {

    private val uid = FirebaseAuth.getInstance().uid!!
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user
    val updatedProfilePicture = user.value?.profilePicture

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _user.postValue(FirestoreService.getUserData(uid))
        }
    }

    fun updateProfile(updatedData: Map<String, Any?>) {
        viewModelScope.launch(Dispatchers.IO) {
            FirestoreService.updateUserData(updatedData, uid)
        }
    }
}