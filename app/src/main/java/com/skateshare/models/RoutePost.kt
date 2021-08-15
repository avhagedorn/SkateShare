package com.skateshare.models

import com.google.firebase.Timestamp
import java.util.*

data class RoutePost(
    val id: String = "",
    val startLat: Double,
    val startLng: Double,
    val lengthMi: Double,
    val lengthKm: Double,
    val boardType: String,
    val terrainType: String,
    val roadType: String,
    val city: String,
    val province: String,
    val country: String,
    var expectedCompletionTime: Double = 0.0,
    val imgUrl: String? = null,
    val distanceToCenter: Double = 0.0,             // Used for radius queries
    override var description: String = "",
    override var posterUsername: String = "",
    override var postProfilePictureUrl: String = "",
    override var datePosted: Timestamp = Timestamp.now(),
    override var isCurrentUser: Boolean = false,
    override var posterId: String = ""
) : FeedItem()


