package com.skateshare.repostitories

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.skateshare.models.User
import com.skateshare.models.User.Companion.newDefaultUser
import com.skateshare.models.User.Companion.toUser
import kotlinx.coroutines.tasks.await

object FirestoreService {

    suspend fun getUserData(uid: String) : User {
        return try {
            FirebaseFirestore.getInstance().collection("users")
                .document(uid).get().await().toUser()
        } catch (e: Exception) {
            Log.e("FirebaseService", e.toString())
            newDefaultUser()
        }
    }
}