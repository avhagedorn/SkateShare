package com.skateshare.repostitories

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.skateshare.interfaces.UserInterface
import com.skateshare.misc.POST_ROUTE
import kotlinx.coroutines.tasks.await

object FirestoreUser : UserInterface {

    override suspend fun getUserData(uid: String) : DocumentSnapshot {
        return FirebaseFirestore.getInstance()
            .document("users/$uid")
            .get()
            .await()
    }

    override suspend fun setUserData(user: HashMap<String, Any?>) {
        val uid = FirebaseAuth.getInstance().uid!!
        FirebaseFirestore.getInstance().collection("users")
            .document(uid).set(user).await()
    }

    override suspend fun updateUserData(user: HashMap<String, Any?>, uid: String) {
        FirebaseFirestore.getInstance().collection("users")
            .document(uid).update(user).await()
    }

    override suspend fun uploadProfilePicture(uid: String, uri: Uri) {
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
    }

    suspend fun deleteUserData(uid: String) {
        val db = FirebaseFirestore.getInstance()
        val storage = FirebaseStorage.getInstance()
        val posts = db.collection("posts")
        val routes = db.collection("routesGlobalMap")

        // Delete all user's posts
        val query = posts
            .whereEqualTo("postedBy", uid)
            .get()
            .await()

        for (doc in query.documents) {
            Log.i("1one", doc["postType"].toString())
            if (doc.getLong("postType")?.toInt() == POST_ROUTE)
                routes.document(doc.id).delete()

            storage.getReference("posts/${doc.id}").delete()
            posts.document(doc.id).delete()
        }

        // Delete board
        db.document("boards/$uid").delete()
        storage.getReference("boards/$uid").delete()

        // Delete user
        db.document("users/$uid").delete()
        storage.getReference("profilePictures/$uid").delete()
    }
}