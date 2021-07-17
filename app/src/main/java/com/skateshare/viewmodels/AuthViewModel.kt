package com.skateshare.viewmodels

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.skateshare.R
import com.skateshare.repostitories.AuthRepository

class AuthViewModelFactory(private val sharedPreferences: SharedPreferences)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java))
            return AuthViewModel(sharedPreferences) as T
        throw IllegalArgumentException("Unknown view model class!")
    }
}

class AuthViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {

    private val authRepository = AuthRepository()
    val loginResponse: LiveData<Task<AuthResult>> = authRepository._loginResponse

    fun register(email: String, password: String, context: Context?) {
        if (credentialsAreValid(email, password, context))
            authRepository.register(email, password)
    }

    fun login(email: String, password: String, context: Context?) {
        if (credentialsAreValid(email, password, context))
            authRepository.login(email, password)
    }

    fun updateLoginStatus(newStatus: Boolean) {
        sharedPreferences.edit().putBoolean("isLoggedIn", newStatus).apply()
    }

    fun getLoginStatus() : Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    private fun credentialsAreValid(email: String, password: String, context: Context?) : Boolean {
        return when {
            email.trim{it<=' '}.isEmpty() -> {
                Toast.makeText(context, R.string.missing_email, Toast.LENGTH_SHORT).show()
                false
            }
            password.trim{it<=' '}.isEmpty() -> {
                Toast.makeText(context, R.string.missing_password, Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

}