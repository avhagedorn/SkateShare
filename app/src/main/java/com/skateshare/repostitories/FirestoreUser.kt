package com.skateshare.repostitories

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.skateshare.R
import com.skateshare.interfaces.UserInterface
import com.skateshare.modelUtils.toUser
import com.skateshare.models.User
import kotlinx.coroutines.tasks.await

object FirestoreUser : UserInterface {

    override suspend fun getUserData(uid: String) : DocumentSnapshot {
        return FirebaseFirestore.getInstance()
            .document("users/$uid")
            .get()
            .await()
    }

    override suspend fun setUserData(user: HashMap<String, Any?>) {
        try {
            val uid = FirebaseAuth.getInstance().uid!!
            FirebaseFirestore.getInstance().collection("users")
                .document(uid).set(user).await()
        } catch(e: Exception) {
            Log.d("FirestorePost", e.toString())
            throw e
        }
    }

    override suspend fun deleteUserData(uid: String) {
        try {
            FirebaseFirestore.getInstance().collection("users")
                .document(uid).delete()
        } catch(e: Exception) {
            Log.d("FirestorePost", e.toString())
            throw e
        }
    }

    override suspend fun updateUserData(user: HashMap<String, Any?>, uid: String) {
        try {
            FirebaseFirestore.getInstance().collection("users")
                .document(uid).update(user).await()
        } catch(e: Exception) {
            Log.d("FirestorePost", e.toString())
            throw e
        }
    }

    override suspend fun uploadProfilePicture(uid: String, uri: Uri) {
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
            Log.e("FirestorePost", e.toString())
            throw e
        }
    }
}