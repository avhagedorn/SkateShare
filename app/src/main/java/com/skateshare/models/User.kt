package com.skateshare.models

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot

data class User (
    val username: String,
    val name: String,
    val bio: String,
    val profilePicture: String
)
