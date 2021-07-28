package com.skateshare.views.feed.recyclerviewcomponents

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skateshare.models.Post

class PostAdapter(val listener: SleepNightListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private final val POST_ITEM = 0
    private final val LOAD_ITEM = 1

    var data = mutableListOf<Post?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LOAD_ITEM -> LoadingViewHolder.from(parent)
            POST_ITEM -> PostViewHolder.from(parent)
            else -> throw Exception("No type match found!")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (data[position]) {
            null -> (holder as LoadingViewHolder)
            else -> (holder as PostViewHolder).bind(data[position]!!, listener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (data[position] == null) LOAD_ITEM else POST_ITEM
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
