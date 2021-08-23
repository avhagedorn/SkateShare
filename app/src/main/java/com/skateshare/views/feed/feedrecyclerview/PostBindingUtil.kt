package com.skateshare.views.feed.feedrecyclerview

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.skateshare.R
import com.skateshare.misc.UNIT_KILOMETERS
import com.skateshare.misc.UNIT_MILES
import com.skateshare.models.FeedItem
import com.skateshare.models.RoutePost
import java.text.DateFormat

@BindingAdapter("itemUsername")
fun TextView.setPostUsername(item: FeedItem) {
    text = item.posterUsername
}

@BindingAdapter("itemDescription")
fun TextView.setPostDescription(item: FeedItem) {
    text = item.description
}

@BindingAdapter("itemProfilePicture")
fun ImageView.setProfilePicture(item: FeedItem) {
    Glide.with(context)
         .load(item.postProfilePictureUrl)
         .circleCrop()
         .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
         .into(this)
}

@BindingAdapter("itemDate")
fun TextView.setPostDate(item: FeedItem) {
    text = DateFormat.getInstance().format(item.datePosted.toDate())
}

@BindingAdapter("settingsVisibility")
fun ImageView.setSettingsVisibility(item: FeedItem) {
    visibility = if (item.isCurrentUser)
        View.VISIBLE
    else
        View.GONE
}

@BindingAdapter("routeHeader")
fun TextView.setRouteHeader(route: RoutePost) {
    text = context.getString(R.string.route_header, route.posterUsername)
}

@BindingAdapter("routeData", "routeUnits", "userAvgSpeed")
fun TextView.setRouteDescription(route: RoutePost, unit: String, avgSpeed: Float) {
    val stringTemplate = context.getString(R.string.route_post_data)
    var distance = 0.0
    var unitString = ""
    val locationString = formatLocationString(context, route)
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
        expectedTime,           // User's expected completion time
        locationString          // Location
    )
}

fun formatLocationString(context: Context, route: RoutePost) : String {
    if (route.country.isEmpty()) return context.getString(R.string.unknown_location)
    if (route.province.isEmpty()) {
        return if (route.city.isEmpty()) route.country
        else "${route.city}, ${route.country}"
    }
    if (route.city.isEmpty()) return "${route.province}, ${route.country}"
    return "${route.city}, ${route.province}, ${route.country}"
}

fun formatExpectedTime(context: Context, route: RoutePost,
                       unit: String, avgSpeed: Float) : String {
    if (avgSpeed == 0f)
        return context.getString(R.string.unknown_time)
    return when (unit) {
        UNIT_MILES -> getTimeString(context, route.lengthMi/avgSpeed)
        UNIT_KILOMETERS -> getTimeString(context, route.lengthKm/avgSpeed)
        else -> context.getString(R.string.unknown_time)
    }
}

fun getTimeString(context: Context, hours: Double) : String {
    return when {
        hours < 1.0 -> context.getString(R.string.minutes_time, hours*60.0)
        hours < 2.0 -> context.getString(R.string.hour_time, (hours-1.0).toInt(), (hours-1.0)*60.0)
        else -> {
            val truncatedHours = hours.toInt()
            context.getString(R.string.hours_time, truncatedHours, (hours-truncatedHours)*60.0)
        }
    }
}