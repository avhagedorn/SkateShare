package com.skateshare.views.feed

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.skateshare.models.Post
import com.skateshare.views.feed.PostDiffCallback
import com.skateshare.views.feed.PostViewHolder
import com.skateshare.views.feed.SleepNightListener

class PostAdapter(val listener: SleepNightListener) : RecyclerView.Adapter<PostViewHolder>() {

    var data = mutableListOf<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(data[position], listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}
