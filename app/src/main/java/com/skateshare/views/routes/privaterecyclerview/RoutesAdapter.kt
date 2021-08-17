package com.skateshare.views.routes.privaterecyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.skateshare.models.Route

class RoutesAdapter(val listener: RouteListener, val unit: String)
    : ListAdapter<Route, RouteViewHolder>(RouteDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        return RouteViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bind(getItem(position), unit, listener)
    }
}