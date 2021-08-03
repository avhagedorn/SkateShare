package com.skateshare.repostitories

import android.net.Uri
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.skateshare.models.User
import com.skateshare.models.User.Companion.newDefaultUser
import com.skateshare.models.User.Companion.toUser
import kotlinx.coroutines.tasks.await
import java.util.HashMap
import javax.inject.Singleton

@Singleton
object FirestoreService {

    /* User functions */
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

    /* Post functions */

    suspend fun getPosts(end: Timestamp) : QuerySnapshot {
        return FirebaseFirestore.getInstance()
            .collection("posts")
            .whereLessThan("datePosted", end)
            .orderBy("datePosted")
            .limitToLast(5)
            .get()
            .await()
    }

    suspend fun createPost(uri: Uri, postData: HashMap<String, Any?>) {
        val postId = postData["id"].toString()
        val imageReference = FirebaseStorage.getInstance()
            .getReference("postPictures/$postId")
        imageReference.putFile(uri).await()
        imageReference.downloadUrl.addOnSuccessListener { newUri ->
            postData["imageUrl"] = newUri.toString()

            FirebaseFirestore.getInstance()
                .document("posts/$postId").set(postData)
        }.await()
    }

    suspend fun deletePost(id: String) {
        FirebaseFirestore.getInstance().document("posts/$id").delete()
        FirebaseStorage.getInstance().getReference("postPictures/$id").delete()
    }
}