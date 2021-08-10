package com.skateshare.views.routes.recyclerviewcomponents

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.skateshare.models.FeedItem
import com.skateshare.models.Route
import com.skateshare.views.feed.recyclerviewcomponents.FeedDiffCallback
import com.skateshare.views.feed.recyclerviewcomponents.ItemViewHolder
import com.skateshare.views.feed.recyclerviewcomponents.SleepNightListener

class RoutesAdapter(val listener: RouteListener, val unit: String)
    : ListAdapter<Route, RouteViewHolder>(RouteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        return RouteViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bind(getItem(position), unit, listener)
    }
}