package com.skateshare.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skateshare.R
import com.skateshare.misc.EventResponse
import com.skateshare.misc.ExceptionResponse
import com.skateshare.repostitories.AuthenticationService
import com.skateshare.repostitories.FirestoreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _loginException = MutableLiveData<ExceptionResponse>()
    val loginException: LiveData<ExceptionResponse> get() = _loginException

    private val _checkCredentialsEmpty = MutableLiveData<EventResponse>()
    val checkCredentialsEmpty: LiveData<EventResponse> get() = _checkCredentialsEmpty

    fun register(email: String, password: String, username: String) {
        val verification = credentialsAreValid(email, password, username)
        if (verification.success) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    AuthenticationService.register(email, password)
                    FirestoreService.setUserData(
                        hashMapOf(
                            "username" to username,
                            "bio" to "This user hasn't told us anything about themselves yet! \uD83D\uDE1E",
                            "name" to "",
                            "profilePicture" to "https://firebasestorage.googleapis.com/v0/b/skateshare-b768a.appspot.com" +
                                                "/o/profilePictures%2FdefaultProfilePicture.png?" +
                                                "alt=media&token=2e7a831a-63d1-4036-a58a-a4704e737a4d"))
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
                    AuthenticationService.login(email, password)
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
            username.trim{it<=' '}.isEmpty() -> {
                EventResponse(R.string.missing_username, false)
            }
            email.trim{it<=' '}.isEmpty() -> {
                EventResponse(R.string.missing_email, false)
            }
            password.trim{it<=' '}.isEmpty() -> {
                EventResponse(R.string.missing_password, false)
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