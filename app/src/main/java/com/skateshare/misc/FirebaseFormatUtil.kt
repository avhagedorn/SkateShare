package com.skateshare.misc

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.Timestamp
import com.skateshare.models.Route
import java.util.*

fun reportToHashMap(location: String, description: String, userId: String) =
    hashMapOf<String, Any?>(
        "postedBy" to location,
        "bugDescription" to description,
        "submittedBy" to userId
    )

fun routeToRoutePost(id: String,
                     uid: String,
                     date: Timestamp,
                     url: String?,
                     description: String,
                     boardType: String,
                     terrainType: String,
                     roadType: String,
                     route: Route,
                     city: String,
                     province: String,
                     country: String,
                     geohash: String) =
    hashMapOf<String, Any?>(
        "id" to id,
        "postedBy" to uid,
        "datePosted" to date,
        "postType" to POST_ROUTE,
        "description" to description,
        "startLat" to route.lat_start,
        "startLng" to route.lng_start,
        "lengthMi" to route.length_mi,
        "lengthKm" to route.length_km,
        "boardType" to boardType,
        "terrainType" to terrainType,
        "roadType" to roadType,
        "city" to city,
        "province" to province,
        "country" to country,
        "geohash" to geohash,
        "imageUrl" to url
    )

fun routeToRoutePath(id: String, date: Timestamp, uid: String, route: Route, geohash: String) =
    hashMapOf<String, Any?>(
        "id" to id,
        "postedBy" to uid,
        "date" to date,
        "lengthMi" to route.length_mi,
        "lengthKm" to route.length_km,
        "encodedPath" to route.path,
        "startLat" to route.lat_start,
        "startLng" to route.lng_start,
        "geohash" to geohash
    )

fun postToHashMap(description: String, uid: String) =
    hashMapOf<String, Any?>(
        "id" to UUID.randomUUID().toString(),
        "description" to description,
        "postedBy" to uid,
        "datePosted" to Timestamp.now()
    )