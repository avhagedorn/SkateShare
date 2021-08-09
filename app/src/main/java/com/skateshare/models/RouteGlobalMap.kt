package com.skateshare.models

import com.google.android.gms.maps.model.PolylineOptions
import java.util.*

// Minimum amount of data to display a clickable polyline on a global map
data class RouteGlobalMap(
    val id : String,
    val date : Date,
    val distanceMi : Double,
    val distanceKm : Double,
    var polyline : PolylineOptions
)
