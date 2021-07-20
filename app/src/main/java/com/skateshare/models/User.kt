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
                User(
                    username = getString("username")!!,
                    name = getString("name")!!,
                    bio = getString("bio")!!,
                    profilePicture = getString("profilePicture")!!)
            } catch (e: Exception) {
                Log.e("User", e.toString())
                newDefaultUser()
            }
        }

        fun newDefaultUser() = User("defaultname", "", "", "")
    }
}