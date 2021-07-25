package com.skateshare.views.feed

import androidx.recyclerview.widget.DiffUtil
import com.skateshare.models.Post

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.tempId == newItem.tempId
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}