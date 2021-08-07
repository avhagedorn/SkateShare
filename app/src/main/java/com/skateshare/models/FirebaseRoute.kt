package com.skateshare.models

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import java.util.*

data class FirebaseRoute(
    val id : String,
    val posted_by : String,
    val date : Date,
    var duration : Long,
    var length_mi : Double,
    var length_km : Double,
    var avg_speed_km : Double,
    var avg_speed_mi : Double,
    var start_location: LatLng,
    var altitude : MutableList<Double>,
    var speed : MutableList<Float>,
    var image_url : String? = null,
    var polyline : PolylineOptions
)
