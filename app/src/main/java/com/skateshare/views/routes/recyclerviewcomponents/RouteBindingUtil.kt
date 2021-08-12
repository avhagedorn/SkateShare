package com.skateshare.views.routes.recyclerviewcomponents

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.skateshare.misc.UNIT_KILOMETERS
import com.skateshare.misc.UNIT_MILES
import com.skateshare.models.Route
import com.skateshare.services.MapHelper
import java.text.DateFormat
import java.util.*

@BindingAdapter("dateFormatter")
fun TextView.dateFormatter(route: Route) {
    text = DateFormat.getDateInstance().format(Date(route.time_start))
}

@BindingAdapter("distanceFormatterRoute", "distanceFormatterUnit")
fun TextView.distanceFormatter(route: Route, unit: String) {
    text = when (unit) {
        UNIT_MILES -> "%.1f Mi".format(route.length_mi)
        UNIT_KILOMETERS -> "%.1f Km".format(route.length_km)
        else -> throw Exception("Invalid units!")
    }
}

@BindingAdapter("durationFormatter")
fun TextView.durationFormatter(route: Route) {
    text = MapHelper.formatTime(route.duration, false)
}

@BindingAdapter("speedFormatterRoute", "speedFormatterUnit")
fun TextView.speedFormatter(route: Route, unit: String) {
    text = when (unit) {
        UNIT_MILES -> "%.1f MPH".format(route.avg_speed_mi)
        UNIT_KILOMETERS -> "%.1f KPH".format(route.avg_speed_km)
        else -> throw Exception("Invalid units!")
    }
}

@SuppressLint("SetTextI18n")
@BindingAdapter("latLngFormatter")
fun TextView.latLngFormatter(route: Route?) {
    text = "${route?.lat_start}, ${route?.lng_start}"
}