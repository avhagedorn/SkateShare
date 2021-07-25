package com.skateshare.models

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot

data class Post(
    val posterId: String,
    val description: String,
    val imageUrl: String,
    val posterUsername: String,
    val tempId: String              // TEMPORARY
    ) {

    companion object {

        fun DocumentSnapshot.toPost() : Post? {
            return try {
                Post(
                    posterId = getString("posterId")!!,
                    description = getString("description")!!,
                    imageUrl = getString("imageUrl")!!,
                    posterUsername = getString("posterUsername")!!,
                    tempId = getString("tempId")!!                  // TEMPORARY
                )
            } catch (e: Exception) {
                Log.d("Post", e.toString())
                null
            }
        }
    }
}
