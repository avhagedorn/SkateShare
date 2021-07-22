package com.skateshare.repostitories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthRepository {

    suspend fun register(email: String, password: String) {
        try {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
        } catch(e: Exception){
            Log.d("AuthRepository", e.toString())
            throw e
        }
    }

    suspend fun login(email: String, password: String) {
        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
        } catch(e: Exception){
            Log.d("AuthRepository", e.toString())
            throw e
        }
    }

    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    suspend fun deleteAccount() {
        try {
            val user = FirebaseAuth.getInstance().currentUser!!
            FirestoreService.deleteUserData(user.uid)
            user.delete()
        }
        catch (e: Exception) {
            Log.d("AuthRepository", e.toString())
        }

    }
}