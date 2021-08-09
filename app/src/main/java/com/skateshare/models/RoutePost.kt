package com.skateshare.models

import com.google.firebase.Timestamp
import java.util.*

data class RoutePost(
    val id: String,
    val startLat: Double,
    val startLng: Double,
    val lengthMi: Double,
    val lengthKm: Double,
    val minBoardType: String,
    val altitudeRating: String,
    var expectedCompletionTime: Double = 0.0,
    val imgUrl: String?,
    override var description: String,
    override var posterUsername: String,
    override var postProfilePictureUrl: String,
    override var datePosted: Timestamp,
    override var isCurrentUser: Boolean = false,
    override var posterId: String
) : FeedItem()


