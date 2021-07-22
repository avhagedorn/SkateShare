package com.skateshare.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.R
import com.skateshare.repostitories.AuthRepository
import com.skateshare.repostitories.FirestoreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    private val _loginException = MutableLiveData<ExceptionResponse>()
    val loginException: LiveData<ExceptionResponse> get() = _loginException

    private val _checkCredentialsEmpty = MutableLiveData<EventResponse>()
    val checkCredentialsEmpty: LiveData<EventResponse> get() = _checkCredentialsEmpty

    fun register(email: String, password: String, username: String) {
        val verification = credentialsAreValid(email, password, username)
        if (verification.success) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    authRepository.register(email, password)
                    FirestoreService.setUserData(
                        hashMapOf(
                            "username" to username,
                            "bio" to "",
                            "name" to "",
                            "profilePicture" to ""))
                    _loginException.postValue(ExceptionResponse(null, true))
                } catch (e: Exception) {
                    _loginException.postValue(ExceptionResponse(e.message, true))
                }
            }
        }
        else
            _checkCredentialsEmpty.value = verification
    }

    fun login(email: String, password: String) {
        // Already-registered users will always have a valid username,
        // so we default to a always-valid placeholder.
        val verification = credentialsAreValid(email, password, "USERNAME")
        if (verification.success)
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    authRepository.login(email, password)
                    _loginException.postValue(ExceptionResponse(null, true))
                } catch (e: Exception) {
                    _loginException.postValue(ExceptionResponse(e.message, true))
                }
            }
        else
            _checkCredentialsEmpty.value = verification
    }

    // Replace EventResponse with nullable int?
    private fun credentialsAreValid(email: String, password: String, username: String) : EventResponse {
        return when {
            email.trim{it<=' '}.isEmpty() -> {
                EventResponse(R.string.missing_email, false)
            }
            password.trim{it<=' '}.isEmpty() -> {
                EventResponse(R.string.missing_password, false)
            }
            username.trim{it<=' '}.isEmpty() -> {
                EventResponse(R.string.missing_username, false)
            }
            else -> EventResponse(R.string.event_passes, true)
        }
    }

    fun resetCredentialError() {
        _checkCredentialsEmpty.value = EventResponse(R.string.event_passes, true)
    }

    fun resetLoginException() {
        _loginException.postValue(ExceptionResponse(null, false))
    }
}