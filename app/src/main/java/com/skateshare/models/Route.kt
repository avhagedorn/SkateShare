package com.skateshare.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.PolylineOptions

// In contrast to FirebaseRoute, Route stores local route data, so data is in a more primitive form,
// without attributes such as LatLng, PolylineOptions, or other such objects.

@Entity(tableName = "my_routes")
data class Route(
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0L,

    @ColumnInfo(name = "start_time_millis")
    val time_start : Long = 0L,

    @ColumnInfo(name="duration_millis")
    var duration : Long = 0L,

    @ColumnInfo(name = "length_mi")
    var length_mi : Double = 0.0,

    @ColumnInfo(name = "length_km")
    var length_km : Double = 0.0,

    @ColumnInfo(name = "avg_speed_km")
    var avg_speed_km : Double = 0.0,

    @ColumnInfo(name = "avg_speed_mi")
    var avg_speed_mi : Double = 0.0,

    @ColumnInfo(name = "start_lat")
    var lat_start : Double = 0.0,

    @ColumnInfo(name = "start_lng")
    var lng_start : Double = 0.0,

    @ColumnInfo(name = "route_lat")
    var lat_path : MutableList<Double>,

    @ColumnInfo(name = "route_lng")
    var lng_path : MutableList<Double>,

    @ColumnInfo(name = "altitude")
    var altitude : MutableList<Double>,

    @ColumnInfo(name = "speed")
    var speed : MutableList<Float>,
)

