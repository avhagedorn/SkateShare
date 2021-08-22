package com.skateshare.modelUtils

import android.location.Location
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
            boardType = getString("boardType")!!,
            terrainType = getString("terrainType")!!,
            roadType = getString("roadType")!!,
            city = getString("city")!!,
            province = getString("province")!!,
            country = getString("country")!!,
            imgUrl = getString("imageUrl")
        )
    } catch (e: Exception) {
        null
    }
}

fun DocumentSnapshot.toLiteRoutePost(centerLat: Double, centerLng: Double) : RoutePost? {
    return try {
        val lat = getDouble("startLat")!!
        val lng = getDouble("startLng")!!
        val distance = FloatArray(1)
        Location.distanceBetween(centerLat, centerLng, lat, lng, distance)

        RoutePost(
            id = getString("id")!!,
            startLat = lat,
            startLng = lng,
            lengthMi = getDouble("lengthMi")!!,
            lengthKm = getDouble("lengthKm")!!,
            boardType = getString("boardType")!!,
            terrainType = getString("terrainType")!!,
            roadType = getString("roadType")!!,
            city = getString("city")!!,
            province = getString("province")!!,
            country = getString("country")!!,
            distanceToCenter = distance[0].toDouble()
        )
    } catch (e: Exception) {
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
        null
    }
}

private fun generatePolyline(path: String) =
    PolylineOptions()
        .addAll(PolyUtil.decode(path))
        .color(POLYLINE_COLOR)
        .width(POLYLINE_WIDTH)
        .clickable(true)