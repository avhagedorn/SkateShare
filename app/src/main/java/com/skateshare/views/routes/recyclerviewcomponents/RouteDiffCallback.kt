package com.skateshare.views.routes.recyclerviewcomponents

import androidx.recyclerview.widget.DiffUtil
import com.skateshare.models.Route

class RouteDiffCallback : DiffUtil.ItemCallback<Route>() {

    override fun areItemsTheSame(oldItem: Route, newItem: Route): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Route, newItem: Route): Boolean {
        return oldItem == newItem
    }
}