package com.skateshare.views.routes.privaterecyclerview

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.skateshare.models.Route
import com.skateshare.models.SimpleFeedItem
import com.skateshare.models.SimpleLoadingItem

class RoutesAdapter(val listener: RouteListener, val unit: String)
    : ListAdapter<SimpleFeedItem, SimpleItemViewHolder>(SimpleFeedDiffCallback()) {

    private val ROUTE_TYPE = 0
    private val LOADING_TYPE = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimpleItemViewHolder {
        return when (viewType) {
            ROUTE_TYPE -> RouteViewHolder.from(parent)
            LOADING_TYPE -> SimpleLoadingViewHolder.from(parent)
            else -> throw Exception("Invalid View Type!")
        }
    }

    override fun onBindViewHolder(holder: SimpleItemViewHolder, position: Int) {
        if (holder is RouteViewHolder)
            holder.bind(getItem(position) as Route, unit, listener)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).item_type
    }
}