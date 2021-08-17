package com.skateshare.models

data class Board(
    val ampHours: Double = 0.0,
    val batteryConfiguration: String = "",
    val topSpeedMph: Double = 0.0,
    val topSpeedKph: Double = 0.0,
    val motorType: String = "",
    val escType: String = "",
    val description: String = "",
    val imageUrl: String = ""
)
