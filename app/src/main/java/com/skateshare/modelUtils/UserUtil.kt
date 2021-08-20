package com.skateshare.modelUtils

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.skateshare.models.User

fun DocumentSnapshot.toUser() : User? {
    return try {
        User(
            username = getString("username")!!,
            name = getString("name")!!,
            bio = getString("bio")!!,
            profilePicture = getString("profilePicture")!!)
    } catch (e: Exception) {
        null
    }
}
