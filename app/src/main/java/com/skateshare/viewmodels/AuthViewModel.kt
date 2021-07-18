package com.skateshare.viewmodels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.*
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.skateshare.R
import com.skateshare.repostitories.AuthRepository
import java.lang.ref.WeakReference

class AuthViewModel : ViewModel() {

    private val authRepository = AuthRepository()
    val loginResponse: LiveData<Task<AuthResult>> = authRepository._loginResponse
    private val _checkCredentialsEmpty = MutableLiveData<EventResponse>()
    val checkCredentialsEmpty: LiveData<EventResponse> = _checkCredentialsEmpty

    fun register(email: String, password: String) {
        val verification = credentialsAreValid(email, password)
        if (verification.passes)
            authRepository.register(email, password)
        else
            _checkCredentialsEmpty.value = verification
    }

    fun login(email: String, password: String) {
        val verification = credentialsAreValid(email, password)
        if (verification.passes)
            authRepository.login(email, password)
        else
            _checkCredentialsEmpty.value = verification
    }

    private fun credentialsAreValid(email: String, password: String) : EventResponse {
        return when {
            email.trim{it<=' '}.isEmpty() -> {
                EventResponse(R.string.missing_email, false)
            }
            password.trim{it<=' '}.isEmpty() -> {
                EventResponse(R.string.missing_password, false)
            }
            else -> EventResponse(R.string.event_passes, true)
        }
    }

}