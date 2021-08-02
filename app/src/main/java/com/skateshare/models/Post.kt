package com.skateshare.models

import com.google.firebase.Timestamp

data class Post(
    val id: String,
    val imageUrl: String,
    val description: String,
    val datePosted: Timestamp,
    val postProfilePictureUrl: String,
    val posterUsername: String,
    val posterId: String,
    var isCurrentUser: Boolean = false) : FeedItem()
