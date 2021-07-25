package com.skateshare.views.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.skateshare.databinding.FeedPostBinding
import com.skateshare.models.Post

class PostViewHolder private constructor(private val binding: FeedPostBinding) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup) : PostViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = FeedPostBinding.inflate(inflater, parent, false)
            return PostViewHolder(binding)
        }
    }

    fun bind(post: Post) {
        binding.post = post
        binding.executePendingBindings()
    }
}