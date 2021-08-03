package com.skateshare.services

import java.util.concurrent.TimeUnit

object MapHelper {

    fun formatTime(ms: Long, includesMs: Boolean = false) : String {
        var millis = ms
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        millis -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
        if (!includesMs)
            return "$hours:${if (minutes < 10) "0" else ""}$minutes:" +
                    "${if (seconds < 10) "0" else ""}$seconds"
        millis /= 10
        return "$hours:${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds:" +
                "${if (millis < 10) "0" else ""}$millis"
    }

    fun metersToStandardUnits(meters: Double) =
        hashMapOf<String, Double>(
            UNIT_MILES to (meters / 1609.344),
            UNIT_KILOMETERS to (meters / 1000.0)
    )
}