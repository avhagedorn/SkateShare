package com.skateshare.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.R
import com.skateshare.models.User
import com.skateshare.repostitories.AuthRepository
import com.skateshare.repostitories.FirestoreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    val loginResponse = authRepository.authError

    private val _checkCredentialsEmpty = MutableLiveData<EventResponse>()
    val checkCredentialsEmpty: LiveData<EventResponse> = _checkCredentialsEmpty

    init {
        Log.i("AuthViewModel", "Created")
    }

    fun register(email: String, password: String, username: String) {
        val verification = credentialsAreValid(email, password, username)
        if (verification.success) {
            viewModelScope.launch(Dispatchers.IO) {
                authRepository.register(email, password)
                Log.d("AuthViewModel", FirebaseAuth.getInstance().uid.toString())
                FirestoreService.setUserData(
                    hashMapOf(
                        "username" to username,
                        "bio" to "",
                        "name" to "",
                        "profilePicture" to ""))
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
                authRepository.login(email, password)
            }
        else
            _checkCredentialsEmpty.value = verification
    }

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

    fun resetCredentialsEmpty() {
        _checkCredentialsEmpty.value = EventResponse(R.string.event_passes, true)
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("AuthViewModel", "Cleared")
    }
}