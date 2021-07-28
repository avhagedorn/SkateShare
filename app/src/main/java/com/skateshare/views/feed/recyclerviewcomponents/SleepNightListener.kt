package com.skateshare.views.feed.recyclerviewcomponents

import com.skateshare.models.Post

class SleepNightListener(val clickListener: (uid: String) -> Unit, val deleteListener: (postId: String) -> Unit) {
    fun onClick(post: Post) = clickListener(post.posterId)
    fun onDelete(post: Post) =deleteListener(post.id)
}