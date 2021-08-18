package com.skateshare.views.routes.privaterecyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.skateshare.R
import com.skateshare.views.feed.feedrecyclerview.ItemViewHolder
import com.skateshare.views.feed.feedrecyclerview.LoadingViewHolder

class SimpleLoadingViewHolder private constructor(private val itemView: View)
    : SimpleItemViewHolder(itemView) {

    companion object {
        fun from(parent: ViewGroup) : SimpleItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.feed_loading, parent, false)
            return SimpleLoadingViewHolder(view)
        }
    }
}