package com.skateshare.models

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.getField

fun reconstructCoordinates(route: Route) : List<LatLng> {
    val routePath = mutableListOf<LatLng>()
    val rawLats = route.lat_path
    val rawLngs = route.lng_path

    for (i in 0 until rawLats.size) {
        routePath.add(LatLng(rawLats[i], rawLngs[i]))
    }

    return routePath
}

fun DocumentSnapshot.toRoute(uid: String) = Route(
        posted_by = uid,
        time_start = getDate("timestamp")!!.time,
        duration = getLong("durationMs")!!,
        length_mi = getDouble("distanceMi")!!,
        length_km = getDouble("distanceKm")!!,
        avg_speed_mi = getDouble("avgSpeedMi")!!,
        avg_speed_km = getDouble("avgSpeedKm")!!,
        lat_start = getDouble("startLat")!!,
        lng_start = getDouble("startLng")!!,
        image_url = getString("imgUrl")!!,
        altitude = getField<MutableList<Double>>("altitude")!!,
        speed = getField<MutableList<Float>>("speed")!!,
        lat_path = getField<MutableList<Double>>("routeLats")!!,
        lng_path = getField<MutableList<Double>>("routeLngs")!!
    )
