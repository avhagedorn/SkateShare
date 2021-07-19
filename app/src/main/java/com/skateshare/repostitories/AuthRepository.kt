package com.skateshare.repostitories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val _authError = MutableLiveData<String>()
    val authError: LiveData<String> = _authError

    suspend fun register(email: String, password: String) {
        try {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
            _authError.postValue(null)
        } catch(e: Exception){
            _authError.postValue(e.message)
        }
    }

    suspend fun login(email: String, password: String) {
        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
            _authError.postValue(null)
        } catch(e: Exception){
            _authError.postValue(e.message)
        }
    }
}