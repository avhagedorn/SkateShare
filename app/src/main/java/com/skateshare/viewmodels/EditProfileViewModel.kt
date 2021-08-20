package com.skateshare.viewmodels

import android.net.Uri
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.misc.ExceptionResponse
import com.skateshare.modelUtils.toUser
import com.skateshare.models.User
import com.skateshare.repostitories.FirestoreUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditProfileViewModel() : ViewModel() {

    private val uid = FirebaseAuth.getInstance().uid!!
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user
    private val _exceptionResponse = MutableLiveData<ExceptionResponse>()
    val exceptionResponse: LiveData<ExceptionResponse> get() = _exceptionResponse

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _user.postValue(FirestoreUser.getUserData(uid).toUser()!!)
        }
    }

    fun updateProfile(updatedData: HashMap<String, Any?>) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                FirestoreUser.updateUserData(updatedData, uid)
                _exceptionResponse.postValue(ExceptionResponse(
                    message = null,
                    isSuccessful = true))
            }
        } catch (e: Exception) {
            _exceptionResponse.postValue(ExceptionResponse(
                e.message,
                isSuccessful = false))
        }
    }

    fun uploadProfilePicture(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                FirestoreUser.uploadProfilePicture(uid, uri)
                _exceptionResponse.postValue(ExceptionResponse(
                    message = null,
                    isSuccessful = true))
            } catch (e: Exception){
                _exceptionResponse.postValue(ExceptionResponse(
                    e.message,
                    isSuccessful = false))
            }
        }
    }

    fun resetResponse() {
        _exceptionResponse.postValue(ExceptionResponse(
            message = null,
            isSuccessful = false,
            isEnabled = false
        ))
    }
}