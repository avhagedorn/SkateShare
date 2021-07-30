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
    private val _exceptionResponse = MutableLiveData<ExceptionResponse>()
    val exceptionResponse: LiveData<ExceptionResponse> get() = _exceptionResponse

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _user.postValue(FirestoreService.getUserData(uid))
        }
    }

    fun updateProfile(updatedData: Map<String, Any?>) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                FirestoreService.updateUserData(updatedData, uid)
                _exceptionResponse.postValue(ExceptionResponse(null, true))
            }
        } catch (e: Exception) {
            _exceptionResponse.postValue(ExceptionResponse(e.message, false))
        }
    }

    fun uploadProfilePicture(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                FirestoreService.uploadProfilePicture(uid, uri)
                _exceptionResponse.postValue(ExceptionResponse(null, true))
            } catch (e: Exception){
                _exceptionResponse.postValue(ExceptionResponse(e.message, false))
            }
        }
    }
}