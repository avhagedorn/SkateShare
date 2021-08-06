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

fun DocumentSnapshot.toRoute() = Route(
        posted_by = getString("postedBy")!!,
        time_start = getDate("timestamp")!!.time,
        duration = getLong("durationMs")!!,
        length_mi = getDouble("distanceMi")!!,
        length_km = getDouble("distanceKm")!!,
        avg_speed_mi = getDouble("avgSpeedMph")!!,
        avg_speed_km = getDouble("avgSpeedKph")!!,
        lat_start = getDouble("startLat")!!,
        lng_start = getDouble("startLng")!!,
        image_url = getString("imgUrl"),
        altitude = get("altitude") as MutableList<Double>,
        speed = get("speed") as MutableList<Float>,
        lat_path = get("routeLats") as MutableList<Double>,
        lng_path = get("routeLngs") as MutableList<Double>
    )
