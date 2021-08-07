package com.skateshare.misc

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
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
        "startLat" to route.lat_start,
        "startLng" to route.lng_start,
        "imgUrl" to null
    )

fun routeToDetailedHashMap(route: Route, previewId: String, posterId: String) =
    hashMapOf<String, Any?>(
        "previewId" to previewId,
        "postedBy" to posterId,
        "timestamp" to Timestamp.now(),
        "durationMs" to route.duration,
        "distanceMi" to route.length_mi,
        "distanceKm" to route.length_km,
        "avgSpeedMph" to route.avg_speed_mi,
        "avgSpeedKph" to route.avg_speed_km,
        "startLat" to route.lat_start,
        "startLng" to route.lng_start,
        "imgUrl" to null,
        "latPath" to route.lat_path,
        "lngPath" to route.lng_path,
        "altitude" to route.altitude,
        "speed" to route.speed,
        "geohash" to GeoFireUtils.getGeoHashForLocation(
            GeoLocation(route.lat_start, route.lng_start))
    )

fun reportToHashMap(location: String, description: String, userId: String) =
    hashMapOf<String, Any?>(
        "postedBy" to location,
        "bugDescription" to description,
        "submittedBy" to userId
    )