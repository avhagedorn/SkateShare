package com.skateshare.modelUtils

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.skateshare.models.Post
import com.skateshare.repostitories.FirestoreUser

suspend fun DocumentSnapshot.toPost(cache: HashMap<String, HashMap<String, String>>) : Post? {
    return try {
        val uid = getString("postedBy")!!
        val userData = getUser(uid, cache)!!
        Post(
            id = getString("id")!!,
            description = getString("description")!!,
            imageUrl = getString("imageUrl")!!,
            datePosted = getTimestamp("datePosted")!!,
            postProfilePictureUrl = userData["profilePicture"]!!,
            posterUsername = userData["username"]!!,
            posterId = uid
        )
    } catch (e: Exception) {
        Log.d("Post", e.toString())
        null
    }
}

suspend fun getUser(uid: String, cache: HashMap<String, HashMap<String, String>>)
                                                : java.util.HashMap<String, String>? {
    if (!cache.containsKey(uid)) {
        val user = FirestoreUser.getUserData(uid).toUser()
        user?.let {
            cache[uid] = hashMapOf<String, String>(
                "username" to user.username,
                "profilePicture" to user.profilePicture
            )
        }
    }
    return cache[uid]
}