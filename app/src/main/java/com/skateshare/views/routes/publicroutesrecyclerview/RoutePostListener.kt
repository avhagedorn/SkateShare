package com.skateshare.views.routes.publicroutesrecyclerview

import com.skateshare.models.RoutePost

class RoutePostListener (val clickListener: (id: String) -> Unit) {

    fun onClick(item: RoutePost) = clickListener(item.id)
}