package com.skateshare.views.feed.recyclerviewcomponents

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skateshare.models.FeedItem
import com.skateshare.models.Post

class FeedAdapter(val listener: SleepNightListener) : ListAdapter<FeedItem, ItemViewHolder>(FeedDiffCallback()) {

    private final val ITEM_POST = 0
    private final val ITEM_LOAD = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return when (viewType) {
            ITEM_POST -> PostViewHolder.from(parent)
            ITEM_LOAD -> LoadingViewHolder.from(parent)
            else -> throw Exception("No type match found!")
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_POST -> (holder as PostViewHolder).bind(getItem(position) as Post, listener)
            ITEM_LOAD -> (holder as LoadingViewHolder) /* Do nothing */
            else -> throw Exception("No type match found!")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Post -> ITEM_POST
            else -> ITEM_LOAD
        }
    }
}
