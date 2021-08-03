package com.skateshare.models

import com.google.android.gms.maps.model.LatLng

fun reconstructCoordinates(route: Route) : List<LatLng> {
    val routePath = mutableListOf<LatLng>()
    val rawLats = route.lat_path
    val rawLngs = route.lng_path

    for (i in 0 until rawLats.size) {
        routePath.add(LatLng(rawLats[i], rawLngs[i]))
    }

    return routePath
}