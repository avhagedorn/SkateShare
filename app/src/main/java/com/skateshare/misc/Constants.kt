package com.skateshare.misc

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng

const val BEGIN_TRACKING = "BEGIN TRACKING"
const val STOP_TRACKING = "STOP TRACKING"

const val CHANNEL_ID = "tracking_channel"
const val CHANNEL_NAME = "Tracking"
const val NOTIFICATION_ID = 1

const val WARNING_CHANNEL_ID = "warning_channel"
const val WARNING_NAME = "Warnings"
const val WARNING_ID = 2

const val SHOW_RECORD_FRAGMENT = "SHOW_RECORD_FRAGMENT"

const val LOGGING_INTERVAL = 2000L
const val FASTEST_INTERVAL = 1500L

const val TIMER_UPDATE_INTERVAL = 50L

// Same color as red_500 in colors.xml
val POLYLINE_COLOR = Color.rgb(244, 67, 54)
const val POLYLINE_WIDTH = 10f
const val MAP_ZOOM = 17f

const val UNIT_MILES = "mi"
const val UNIT_KILOMETERS = "km"

const val MAX_RADIUS_METERS = 20
const val MAX_ZOOM_RADIUS = 591657550.5

const val MIN_ZOOM_QUERY = 12f

val DEFAULT_LOCATION = LatLng(
    44.974526,
    -93.232064
)

// Post types
const val POST_LOADING = 0
const val POST_MEDIA = 1
const val POST_ROUTE = 2

const val SHORTBOARD = "Shortboard"
const val LONGBOARD = "Longboard"
const val MOUNTAINBOARD = "Mountainboard"

const val LOW_HILLS = "Nearly Flat"
const val MEDIUM_HILLS = "Moderately Hilly"
const val HIGH_HILLS = "Very Hilly"

const val QUERY_LIMIT = 10
const val BY_DATE = 0
const val BY_DISTANCE = 1
const val BY_DURATION = 2
const val BY_SPEED = 3
