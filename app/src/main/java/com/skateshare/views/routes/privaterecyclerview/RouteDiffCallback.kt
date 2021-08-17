package com.skateshare.views.routes.privaterecyclerview

import androidx.recyclerview.widget.DiffUtil
import com.skateshare.models.Route

class RouteDiffCallback : DiffUtil.ItemCallback<Route>() {

    override fun areItemsTheSame(oldItem: Route, newItem: Route): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Route, newItem: Route): Boolean {
        return oldItem == newItem
    }
}