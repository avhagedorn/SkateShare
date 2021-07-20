package com.skateshare.repostitories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val _authStatus = MutableLiveData<String?>()
    val authStatus: LiveData<String?> = _authStatus

    suspend fun register(email: String, password: String) {
        try {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
            _authStatus.postValue(null)
        } catch(e: Exception){
            _authStatus.postValue(e.message)
        }
    }

    suspend fun login(email: String, password: String) {
        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
            _authStatus.postValue(null)
        } catch(e: Exception){
            _authStatus.postValue(e.message)
        }
    }
}