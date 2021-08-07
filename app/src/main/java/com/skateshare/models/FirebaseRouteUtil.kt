package com.skateshare.models

import android.graphics.Paint
import android.util.Log
import com.google.android.gms.maps.model.Cap
import com.google.android.gms.maps.model.JointType.ROUND
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.firestore.DocumentSnapshot
import com.skateshare.services.POLYLINE_COLOR
import com.skateshare.services.POLYLINE_WIDTH
import java.lang.Exception

fun DocumentSnapshot.toFirebaseRoute() : FirebaseRoute {
    return try {
        val latPath = get("latPath") as MutableList<Double>
        val lngPath = get("lngPath") as MutableList<Double>
        FirebaseRoute(
            id = getString("previewId")!!,
            posted_by = getString("postedBy")!!,
            date = getDate("timestamp")!!,
            duration = getLong("durationMs")!!,
            length_mi = getDouble("distanceMi")!!,
            length_km = getDouble("distanceKm")!!,
            avg_speed_mi = getDouble("avgSpeedMph")!!,
            avg_speed_km = getDouble("avgSpeedKph")!!,
            start_location = LatLng(latPath[0], lngPath[0]),
            image_url = getString("imgUrl"),
            altitude = get("altitude") as MutableList<Double>,
            speed = get("speed") as MutableList<Float>,
            polyline = generatePolyline(latPath, lngPath)
        )
    } catch (e: Exception) {
        Log.i("1one412", e.message.toString())
        throw e
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
