package com.skateshare.views.routes.publicroutesrecyclerview

import com.skateshare.models.RoutePost

class RoutePostListener (val clickListener: (lat: Float, lng: Float) -> Unit) {

    fun onClick(item: RoutePost) = clickListener(item.startLat.toFloat(), item.startLng.toFloat())
}