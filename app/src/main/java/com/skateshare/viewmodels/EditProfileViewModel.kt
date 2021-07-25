package com.skateshare.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.models.User
import com.skateshare.repostitories.FirestoreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val uid = FirebaseAuth.getInstance().uid!!
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    private val _response = MutableLiveData<String?>()
    val response: LiveData<String?> = _response

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _user.postValue(FirestoreService.getUserData(uid))
        }
    }

    fun updateProfile(updatedData: Map<String, Any?>) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                FirestoreService.updateUserData(updatedData, uid)
                _response.postValue(null)
            }
        } catch (e: Exception) {
            _response.postValue(e.message)
        }
    }

    fun uploadProfilePicture(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                FirestoreService.uploadProfilePicture(uid, uri)
                _response.postValue(null)
            } catch (e: Exception){
                _response.postValue(e.message)
            }
        }
    }
}