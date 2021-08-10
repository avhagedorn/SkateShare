package com.skateshare.modelUtils

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.google.maps.android.PolyUtil
import com.skateshare.misc.POLYLINE_COLOR
import com.skateshare.misc.POLYLINE_WIDTH
import com.skateshare.models.RouteGlobalMap
import com.skateshare.models.RoutePost

suspend fun DocumentSnapshot.toRoutePost(cache: HashMap<String, HashMap<String, String>>) : RoutePost? {
    return try {
        val uid = getString("postedBy")!!
        val userData = getUser(uid, cache)!!
        RoutePost(
            id = getString("id")!!,
            posterId = uid,
            postProfilePictureUrl = userData["profilePicture"]!!,
            posterUsername = userData["username"]!!,
            datePosted = getTimestamp("datePosted")!!,
            description = getString("description")!!,
            startLat = getDouble("startLat")!!,
            startLng = getDouble("startLng")!!,
            lengthMi = getDouble("lengthMi")!!,
            lengthKm = getDouble("lengthKm")!!,
            minBoardType = getString("minBoardType")!!,
            altitudeRating = getString("altitudeRating")!!,
            imgUrl = getString("imageUrl")
        )
    } catch (e: Exception) {
        Log.i("RouteUtil", e.message.toString())
        null
    }
}

fun DocumentSnapshot.toRouteGlobalMap() : RouteGlobalMap? {
    return try {
        val encodedPath = getString("encodedPath")!!

        RouteGlobalMap(
            id = getString("id")!!,
            date = getDate("date")!!,
            distanceMi = getDouble("lengthMi")!!,
            distanceKm = getDouble("lengthKm")!!,
            polyline = generatePolyline(encodedPath)
        )
    } catch (e: Exception) {
        Log.i("RouteUtil", e.message.toString())
        null
    }
}

private fun generatePolyline(path: String) =
    PolylineOptions()
        .addAll(PolyUtil.decode(path))
        .color(POLYLINE_COLOR)
        .width(POLYLINE_WIDTH)
        .clickable(true)