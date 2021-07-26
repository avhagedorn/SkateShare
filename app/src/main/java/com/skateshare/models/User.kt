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

        fun createUserData(username: String) =
            hashMapOf(
                "username" to username,
                "bio" to "This user hasn't told us anything about themselves yet! \uD83D\uDE1E",
                "name" to "",
                "profilePicture" to "https://firebasestorage.googleapis.com/v0/b/skateshare-b768a.appspot.com" +
                        "/o/profilePictures%2FdefaultProfilePicture.png?" +
                        "alt=media&token=2e7a831a-63d1-4036-a58a-a4704e737a4d")
    }
}