package com.skateshare.repostitories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    suspend fun deleteAccount() {
        try {
            val user = FirebaseAuth.getInstance().currentUser!!

            Log.d("DeleteAccount", user.uid)

            FirestoreService.deleteUserData(user.uid)
            user.delete()
        }
        catch (e: Exception) {
            Log.d("AuthRepository", e.toString())
        }

    }
}