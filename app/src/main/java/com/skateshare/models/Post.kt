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
    val posterId: String,
    var isCurrentUser: Boolean = false) : FeedItem()
