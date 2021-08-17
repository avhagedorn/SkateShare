package com.skateshare.views.routes.privaterecyclerview

import com.skateshare.models.Route

class RouteListener(val clickListener: (id: Long) -> Unit,
                    val deleteListener: (index: Int, route: Route) -> Unit) {

    fun onClick(item: Route) = clickListener(item.id)
    fun onDelete(index: Int, route: Route) = deleteListener(index, route)
}