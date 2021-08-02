package com.skateshare.views.feed.recyclerviewcomponents

import androidx.recyclerview.widget.DiffUtil
import com.skateshare.models.FeedItem

class FeedDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }
}