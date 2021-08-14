package com.skateshare.repostitories

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.skateshare.interfaces.AuthenticationInterface
import kotlinx.coroutines.tasks.await

object FirebaseAuthentication : AuthenticationInterface {

    override suspend fun register(email: String, password: String) {
        try {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
        } catch(e: Exception){
            Log.d("FirebaseAuthentication", e.toString())
            throw e
        }
    }

    override suspend fun login(email: String, password: String) {
        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
        } catch(e: Exception){
            Log.d("FirebaseAuthentication", e.toString())
            throw e
        }
    }

    override fun logout() {
        FirebaseAuth.getInstance().signOut()
    }

    override suspend fun deleteAccount() {
        try {
            val user = FirebaseAuth.getInstance().currentUser!!
            FirestoreUser.deleteUserData(user.uid)
            user.delete()
        }
        catch (e: Exception) {
            Log.d("FirebaseAuthentication", e.toString())
        }

    }
}