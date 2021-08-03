package com.skateshare.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.type.LatLng
import java.security.Timestamp

@Entity(tableName = "my_routes")
data class Route(
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0L,

    @ColumnInfo(name = "start_time_millis")
    val time_start : Long = 0L,

    @ColumnInfo(name = "end_time_millis")
    var time_stop : Long = 0L,

    @ColumnInfo(name="duration_millis")
    var duration : Long = time_stop - time_start,

    @ColumnInfo(name = "length_mi")
    var length_mi : Double = 0.0,

    @ColumnInfo(name = "length_km")
    var length_km : Double = 0.0,

    @ColumnInfo(name = "avg_speed")
    var avg_speed: Double = 0.0,

    @ColumnInfo(name = "route_lat")
    var lat_path : MutableList<Double>,

    @ColumnInfo(name = "route_lng")
    var lng_path : MutableList<Double>,

    @ColumnInfo(name = "altitude")
    var altitude : MutableList<Double>,

    @ColumnInfo(name = "speed")
    var speed : MutableList<Double>,

    @ColumnInfo(name = "image")
    var image_url : String? = null
)

