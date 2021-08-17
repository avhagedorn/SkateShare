package com.skateshare.modelUtils

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.skateshare.models.Board

fun DocumentSnapshot.toBoard() : Board? {
    return try {
        Board (
            ampHours = getDouble("ampHours")!!,
            batteryConfiguration = getString("batteryConfig")!!,
            escType = getString("escType")!!,
            motorType = getString("motorType")!!,
            topSpeedMph = getDouble("topSpeedMph")!!,
            topSpeedKph = getDouble("topSpeedKph")!!,
            description = getString("description")!!,
            imageUrl = getString("imageUrl")!!
        )
    } catch (e: Exception) {
        null
    }
}