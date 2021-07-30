package com.skateshare.views.feed.recyclerviewcomponents

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skateshare.models.Post

class PostAdapter(val listener: SleepNightListener) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return when (viewType) {
//            LOAD_ITEM -> LoadingViewHolder.from(parent)
//            POST_ITEM -> PostViewHolder.from(parent)
//            else -> throw Exception("No type match found!")
//        }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder.from(parent)
    }

//    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
//        when (data[position]) {
//            null -> (holder as LoadingViewHolder)
//            else -> (holder as PostViewHolder).bind(data[position]!!, listener)
//        }
//    }
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }
}
