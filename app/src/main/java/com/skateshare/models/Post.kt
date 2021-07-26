package com.skateshare.models

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.skateshare.repostitories.FirestoreService

data class Post(
    val id: String,
    val imageUrl: String,
    val description: String,
    val datePosted: Timestamp,
    val postProfilePictureUrl: String,
    val posterUsername: String,
    val posterId: String) {

    companion object {

        suspend fun DocumentSnapshot.toPost() : Post? {
            return try {
                val uid = getString("postedBy")
                val user = FirestoreService.getUserData(uid!!)
                Post(
                    id = getString("id")!!,
                    description = getString("description")!!,
                    imageUrl = getString("imageUrl")!!,
                    datePosted = getTimestamp("datePosted")!!,
                    postProfilePictureUrl = user.profilePicture,
                    posterUsername = user.username,
                    posterId = uid
                )
            } catch (e: Exception) {
                Log.d("Post", e.toString())
                null
            }
        }
    }
}
