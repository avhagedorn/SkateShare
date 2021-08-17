package com.skateshare.views.feed.feedrecyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.skateshare.misc.POST_LOADING
import com.skateshare.misc.POST_MEDIA
import com.skateshare.misc.POST_ROUTE
import com.skateshare.models.FeedItem
import com.skateshare.models.Post
import com.skateshare.models.RoutePost

class FeedAdapter(val listener: FeedItemListener, val units: String, val avgSpeed: Float)
    : ListAdapter<FeedItem, ItemViewHolder>(FeedDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return when (viewType) {
            POST_MEDIA -> PostViewHolder.from(parent)
            POST_ROUTE -> RoutePostViewHolder.from(parent)
            POST_LOADING -> LoadingViewHolder.from(parent)
            else -> throw Exception("No type match found!")
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (getItemViewType(position)) {
            POST_MEDIA -> (holder as PostViewHolder).bind(getItem(position) as Post, listener)
            POST_ROUTE -> (holder as RoutePostViewHolder)
                .bind(getItem(position) as RoutePost, units, avgSpeed, listener)
            POST_LOADING -> (holder as LoadingViewHolder) /* Do nothing */
            else -> throw Exception("No type match found!")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Post -> POST_MEDIA
            is RoutePost -> POST_ROUTE
            else -> POST_LOADING
        }
    }
}
