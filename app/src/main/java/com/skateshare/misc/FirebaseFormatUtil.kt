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
                     country: String) =
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
        "imageUrl" to url
    )

fun routeToRoutePath(id: String, date: Timestamp, uid: String, route: Route, path: String) =
    hashMapOf<String, Any?>(
        "id" to id,
        "postedBy" to uid,
        "date" to date,
        "accuracy" to route.accuracy,       // TODO: REMOVE ME
        "lengthMi" to route.length_mi,
        "lengthKm" to route.length_km,
        "encodedPath" to path,
        "startLat" to route.lat_start,
        "startLng" to route.lng_start,
        "geohash" to GeoFireUtils.getGeoHashForLocation(
            GeoLocation(route.lat_start, route.lng_start))
    )
