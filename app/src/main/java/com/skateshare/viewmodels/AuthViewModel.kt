package com.skateshare.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skateshare.R
import com.skateshare.misc.EventResponse
import com.skateshare.misc.ExceptionResponse
import com.skateshare.repostitories.FirebaseAuthentication
import com.skateshare.repostitories.FirestoreUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _loginResponse = MutableLiveData<ExceptionResponse>()
    val loginResponse: LiveData<ExceptionResponse> get() = _loginResponse

    private val _checkCredentialsEmpty = MutableLiveData<EventResponse>()
    val checkCredentialsEmpty: LiveData<EventResponse> get() = _checkCredentialsEmpty

    fun register(email: String, password: String, username: String, userData: HashMap<String, Any?>) {
        val verification = credentialsAreValid(email, password, username)
        if (verification.success) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    FirebaseAuthentication.register(email, password)
                    FirestoreUser.setUserData(userData)
                    _loginResponse.postValue(ExceptionResponse(
                        message = null,
                        isSuccessful = true))
                } catch (e: Exception) {
                    _loginResponse.postValue(ExceptionResponse(
                        e.message,
                        isSuccessful = true))
                }
            }
        }
        else
            _checkCredentialsEmpty.postValue(verification)
    }

    fun login(email: String, password: String) {
        // Already-registered users will always have a valid username,
        // so we default to a always-valid placeholder.
        val verification = credentialsAreValid(email, password, "USERNAME")
        if (verification.success)
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    FirebaseAuthentication.login(email, password)
                    _loginResponse.postValue(ExceptionResponse(
                        message = null,
                        isSuccessful = true))
                } catch (e: Exception) {
                    _loginResponse.postValue(ExceptionResponse(
                        e.message,
                        isSuccessful = false))
                }
            }
        else
            _checkCredentialsEmpty.value = verification
    }

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
        _loginResponse.postValue(ExceptionResponse(
            message = null,
            isSuccessful = false,
            isEnabled = false))
    }
}