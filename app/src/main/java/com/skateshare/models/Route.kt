package com.skateshare.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_routes")
data class Route(
    @PrimaryKey(autoGenerate = true)
    override var id : Long = 0L,

    @ColumnInfo(name = "item_type")
    override val item_type : Int = 0,

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
    var lat_path : MutableList<Double> = mutableListOf(),

    @ColumnInfo(name = "route_lng")
    var lng_path : MutableList<Double> = mutableListOf(),

    @ColumnInfo(name = "route_accuracy")
    var accuracy : MutableList<Float> = mutableListOf(),

    @ColumnInfo(name = "speed")
    var speed : MutableList<Float> = mutableListOf(),

    @ColumnInfo(name = "is_public")
    var isPublic : Boolean = false

) : SimpleFeedItem() {

    override fun equals(other: Any?): Boolean {
        return other is Route
                && duration == other.duration
                && length_mi == other.length_mi
                && lat_start == other.lat_start
                && lng_start == other.lng_start
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + time_start.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + length_mi.hashCode()
        result = 31 * result + length_km.hashCode()
        result = 31 * result + avg_speed_km.hashCode()
        result = 31 * result + avg_speed_mi.hashCode()
        result = 31 * result + lat_start.hashCode()
        result = 31 * result + lng_start.hashCode()
        result = 31 * result + lat_path.hashCode()
        result = 31 * result + lng_path.hashCode()
        result = 31 * result + accuracy.hashCode()
        result = 31 * result + speed.hashCode()
        result = 31 * result + isPublic.hashCode()
        return result
    }
}

