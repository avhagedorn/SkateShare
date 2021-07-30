package com.skateshare.views.feed.recyclerviewcomponents

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skateshare.R

class LoadingViewHolder private constructor(private val itemView: View) : ItemViewHolder(itemView) {

    companion object {
        fun from(parent: ViewGroup) : LoadingViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.feed_loading, parent, false)
            return LoadingViewHolder(view)
        }
    }
}