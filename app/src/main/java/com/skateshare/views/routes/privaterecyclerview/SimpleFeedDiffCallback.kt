package com.skateshare.views.routes.privaterecyclerview

import androidx.recyclerview.widget.DiffUtil
import com.skateshare.models.LoadingItem
import com.skateshare.models.Route
import com.skateshare.models.SimpleFeedItem
import com.skateshare.models.SimpleLoadingItem

class SimpleFeedDiffCallback : DiffUtil.ItemCallback<SimpleFeedItem>() {

    override fun areItemsTheSame(oldItem: SimpleFeedItem, newItem: SimpleFeedItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SimpleFeedItem, newItem: SimpleFeedItem): Boolean {
        return if (oldItem is Route)
            if (newItem is Route) oldItem as Route == newItem as Route else false
        else
            newItem is SimpleLoadingItem
    }
}