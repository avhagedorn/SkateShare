package com.skateshare.models

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot

data class User (
    val username: String,
    val name: String,
    val bio: String,
    val profilePicture: String) {

    companion object {

        fun DocumentSnapshot.toUser() : User {
            return try {
                val username = getString("username")!!
                val name = getString("name")!!
                val bio = getString("bio")!!
                val profilePicture = getString("profilePicture")!!
                Log.d("User", username)
                User(username, name, bio, profilePicture)
            } catch (e: Exception) {
                Log.e("User", e.toString())
                newDefaultUser()
            }
        }

        fun newDefaultUser() = User("", "", "", "")
    }
}