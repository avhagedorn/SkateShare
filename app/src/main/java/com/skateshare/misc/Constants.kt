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
const val FASTEST_INTERVAL = 2000L

const val TIMER_UPDATE_INTERVAL = 50L

// Same color as red_500 in colors.xml
val POLYLINE_COLOR = Color.rgb(244, 67, 54)
const val POLYLINE_WIDTH = 10f
const val MAP_ZOOM = 17f

const val UNIT_MILES = "mi"
const val UNIT_KILOMETERS = "km"

const val MAX_RADIUS_METERS = 30
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

const val SMOOTH_ROADS = "Smooth Roads"
const val AVERAGE_ROADS = "Average Roads"
const val ROUGH_ROADS = "Rough Roads"
const val NO_ROADS = "Trail"

const val QUERY_LIMIT = 10

// Private route sorting
const val BY_DATE = 0
const val BY_DISTANCE = 1
const val BY_DURATION = 2
const val BY_SPEED = 3

// Public route sorting

const val METERS_SEC_TO_MI_HR = 2.236936f
const val METERS_SEC_TO_KM_HR = 3.6f

const val METERS_IN_MILE = 1609.344
const val METERS_IN_KILOMETER = 1000

// Radius sizes, in Meters
const val SMALL_RADIUS_MI = 1609.344        // 1 Mile
const val MEDIUM_RADIUS_MI = 3218.688       // 2 Miles
const val LARGE_RADIUS_MI = 8046.72         // 5 Miles

const val SMALL_RADIUS_KM = 1000.0          // 1 Kilometer
const val MEDIUM_RADIUS_KM = 2000.0         // 2 Kilometers
const val LARGE_RADIUS_KM = 5000.0          // 5 Kilometers

const val SMALL_RADIUS_POSITION = 0
const val MEDIUM_RADIUS_POSITION = 1
const val LARGE_RADIUS_POSITION = 2