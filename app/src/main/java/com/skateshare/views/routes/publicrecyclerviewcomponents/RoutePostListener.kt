package com.skateshare.views.routes.publicrecyclerviewcomponents

import com.skateshare.models.Route
import com.skateshare.models.RoutePost

class RoutePostListener (val clickListener: (id: String) -> Unit) {

    fun onClick(item: RoutePost) = clickListener(item.id)
}