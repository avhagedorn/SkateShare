package com.skateshare.views.Feed

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skateshare.models.Post

class PostAdapter : RecyclerView.Adapter<PostViewHolder>() {

    var posts = listOf<Post?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        if (post != null)
            holder.bind(post)
    }

    override fun getItemCount() = posts.size
}