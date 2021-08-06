package com.skateshare.services

import android.graphics.Color

const val BEGIN_TRACKING = "BEGIN TRACKING"
const val STOP_TRACKING = "STOP TRACKING"

const val CHANNEL_ID = "tracking_channel"
const val CHANNEL_NAME = "Tracking"
const val NOTIFICATION_ID = 1

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

const val MAX_RADIUS_METERS = 30
const val MAX_ZOOM_RADIUS = 591657550.5