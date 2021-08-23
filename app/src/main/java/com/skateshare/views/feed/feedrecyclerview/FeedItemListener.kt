package com.skateshare.views.feed.feedrecyclerview

import com.skateshare.models.FeedItem
import com.skateshare.models.Post
import com.skateshare.models.RoutePost

class FeedItemListener(val clickUserListener: (uid: String) -> Unit,
                       val deleteListener: (id: String, type: Int, pos: Int) -> Unit,
                       val clickRouteListener: (lat: Float, lng: Float) -> Unit) {

    fun onClickUser(item: FeedItem) = clickUserListener(item.posterId)
    fun onClickRoute(route: RoutePost) = clickRouteListener(
        route.startLat.toFloat(), route.startLng.toFloat()
    )
}