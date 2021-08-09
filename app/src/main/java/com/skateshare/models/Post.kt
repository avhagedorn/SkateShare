package com.skateshare.models

import com.google.firebase.Timestamp
import java.util.*

data class Post(
    val id: String,
    val imageUrl: String,
    override var description: String,
    override var postProfilePictureUrl: String,
    override var posterUsername: String,
    override var datePosted: Timestamp,
    override var posterId: String,
    override var isCurrentUser: Boolean = false
) : FeedItem()
