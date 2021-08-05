package com.skateshare.misc

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Timestamp
import com.skateshare.models.Route
import java.util.*

fun routeToPreviewHashMap(route: Route, posterId: String) =
    hashMapOf<String, Any?>(
        "id" to UUID.randomUUID().toString(),
        "postedBy" to posterId,
        "timestamp" to Timestamp.now(),
        "durationMs" to route.duration,
        "distanceMi" to route.length_mi,
        "distanceKm" to route.length_km,
        "avgSpeedMph" to route.avg_speed_mi,
        "avgSpeedKph" to route.avg_speed_km,
        "startCoordinates" to LatLng(route.lat_path[0], route.lng_path[0]),
        "imgUrl" to null
    )

fun routeToDetailedHashMap(route: Route, previewId: String, posterId: String) =
    hashMapOf<String, Any?>(
        "previewId" to previewId,
        "postedBy" to posterId,
        "routePath" to toLatLng(route.lat_path, route.lng_path),
        "altitude" to route.altitude,
        "speed" to route.speed
    )

fun toLatLng(lats: List<Double>, lngs: List<Double>) : List<LatLng> {
    val coordinates = mutableListOf<LatLng>()
    for (i in lats.indices) {
        coordinates.add(LatLng(lats[i], lngs[i]))
    }
    return coordinates
}