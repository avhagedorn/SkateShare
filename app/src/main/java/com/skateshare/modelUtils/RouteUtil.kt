package com.skateshare.modelUtils

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.skateshare.misc.POLYLINE_COLOR
import com.skateshare.misc.POLYLINE_WIDTH
import com.skateshare.models.RouteGlobalMap
import com.skateshare.models.RoutePost
import com.skateshare.repostitories.FirestoreUser.getUserData

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
        val lats = get("latPath") as List<Double>
        val lngs = get("lngPath") as List<Double>

        RouteGlobalMap(
            id = getString("id")!!,
            date = getDate("date")!!,
            distanceMi = getDouble("lengthMi")!!,
            distanceKm = getDouble("lengthKm")!!,
            polyline = generatePolyline(lats, lngs)
        )
    } catch (e: Exception) {
        Log.i("RouteUtil", e.message.toString())
        null
    }
}

private fun generatePolyline(lats: List<Double>, lngs: List<Double>) =
    PolylineOptions()
        .addAll(getLatLngPath(lats, lngs))
        .color(POLYLINE_COLOR)
        .width(POLYLINE_WIDTH)
        .clickable(true)

private fun getLatLngPath(lats: List<Double>, lngs: List<Double>) : List<LatLng> {
    val output = mutableListOf<LatLng>()
    for (i in lats.indices) {
        output.add(LatLng(lats[i], lngs[i]))
    }
    return output
}