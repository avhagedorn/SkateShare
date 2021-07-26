package com.skateshare.repostitories

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storageMetadata
import com.skateshare.models.User
import com.skateshare.models.User.Companion.newDefaultUser
import com.skateshare.models.User.Companion.toUser
import kotlinx.coroutines.tasks.await
import java.util.*

object FirestoreService {

    suspend fun getUserData(uid: String) : User {
        return try {
            FirebaseFirestore.getInstance().collection("users")
                .document(uid).get().await().toUser()
        } catch (e: Exception) {
            Log.d("FirestoreService", e.toString())
            newDefaultUser()
        }
    }

    suspend fun setUserData(user: Map<String, Any?>) {
        try {
            val uid = FirebaseAuth.getInstance().uid!!
            FirebaseFirestore.getInstance().collection("users")
                .document(uid).set(user).await()
        } catch(e: Exception) {
            Log.d("FirestoreService", e.toString())
            throw e
        }
    }

    suspend fun deleteUserData(uid: String) {
        try {
            FirebaseFirestore.getInstance().collection("users")
                .document(uid).delete()
        } catch(e: Exception) {
            Log.d("FirestoreService", e.toString())
            throw e
        }
    }

    suspend fun updateUserData(user: Map<String, Any?>, uid: String) {
        try {
            FirebaseFirestore.getInstance().collection("users")
                .document(uid).update(user).await()
        } catch(e: Exception) {
            Log.d("FirestoreService", e.toString())
            throw e
        }
    }

    suspend fun uploadProfilePicture(uid: String, uri: Uri) {
        try {
            val imageReference = FirebaseStorage.getInstance()
                .getReference("profilePictures/$uid")
            // Upload file
            imageReference.putFile(uri).addOnSuccessListener {
                imageReference.downloadUrl.addOnSuccessListener { newUri ->
                    // Save profile picture
                    FirebaseFirestore.getInstance().collection("users")
                        .document(uid).update(
                            hashMapOf<String, Any?>(
                                "profilePicture" to newUri.toString()))
                }
            }
        } catch(e: Exception) {
            Log.e("FirestoreService", e.toString())
            throw e
        }
    }
}