package com.skateshare.services

import com.skateshare.misc.UNIT_KILOMETERS
import com.skateshare.misc.UNIT_MILES
import java.util.concurrent.TimeUnit

object MapHelper {

    fun formatTime(s: Long) : String {
        var sec = s
        val hours = TimeUnit.SECONDS.toHours(sec)
        sec -= TimeUnit.HOURS.toSeconds(hours)
        val minutes = TimeUnit.SECONDS.toMinutes(sec)
        sec -= TimeUnit.MINUTES.toSeconds(minutes)
        return "$hours:${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (sec < 10) "0" else ""}$sec"
    }

    fun metersToStandardUnits(meters: Double) =
        hashMapOf<String, Double>(
            UNIT_MILES to (meters / 1609.344),
            UNIT_KILOMETERS to (meters / 1000.0)
        )

    fun metersToStandardSpeed(meters: Float, unit: String) =
        when (unit) {
            UNIT_KILOMETERS -> "%.1f km/hr".format(meters * 3.6)
            UNIT_MILES -> "%.1f mi/hr".format(meters * 2.23694)
            else -> "UNDEFINED"
        }

    fun metersToFormattedUnits(meters: Double, unit: String) =
        when (unit) {
            UNIT_KILOMETERS -> "%.1f km".format(meters*1000)
            UNIT_MILES -> "%.1f mi".format(meters/1609.344)
            else -> "UNDEFINED"
        }

    fun calculateAvgSpeed(meters: Double, duration: Long) =
        hashMapOf(
            UNIT_MILES to (meters * 2236.936 / duration),
            UNIT_KILOMETERS to (meters * 3600 / duration)
        )
}