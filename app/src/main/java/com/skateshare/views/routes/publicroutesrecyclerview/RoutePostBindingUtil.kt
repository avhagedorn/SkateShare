package com.skateshare.views.routes.publicroutesrecyclerview

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.skateshare.R
import com.skateshare.misc.METERS_IN_KILOMETER
import com.skateshare.misc.METERS_IN_MILE
import com.skateshare.misc.UNIT_KILOMETERS
import com.skateshare.misc.UNIT_MILES
import com.skateshare.models.RoutePost
import com.skateshare.views.feed.feedrecyclerview.formatExpectedTime

@BindingAdapter("distanceTo", "distanceUnits")
fun TextView.distanceTo(route: RoutePost, unit: String) {
    text = when (unit) {
        UNIT_MILES -> context.getString(R.string.miles_away)
            .format(route.distanceToCenter/METERS_IN_MILE)
        UNIT_KILOMETERS -> context.getString(R.string.kilometers_away)
            .format(route.distanceToCenter/METERS_IN_KILOMETER)
        else -> ""
    }
}

@BindingAdapter("location")
fun TextView.location(route: RoutePost) {
    text = when {
        route.city.isNotEmpty() -> route.city
        route.province.isNotEmpty() -> route.province
        else -> route.country
    }
}

@BindingAdapter("routePostData", "unit", "avgSpeed")
fun TextView.setLiteTags(route: RoutePost, unit: String, avgSpeed: Float) {
    val stringTemplate = context.getString(R.string.route_post_data_no_location)
    var distance = 0.0
    var unitString = ""
    val expectedTime = formatExpectedTime(context, route, unit, avgSpeed)

    when (unit) {
        UNIT_MILES -> {
            distance = route.lengthMi
            unitString = "Miles"
        }
        UNIT_KILOMETERS -> {
            distance = route.lengthKm
            unitString = "Kilometers"
        }
    }

    text = stringTemplate.format(
        distance,               // Distance
        unitString,             // Units
        route.terrainType,      // Terrain Quality
        route.roadType,         // Road Quality
        route.boardType,        // Board Type
        expectedTime            // User's expected completion time
    )
}