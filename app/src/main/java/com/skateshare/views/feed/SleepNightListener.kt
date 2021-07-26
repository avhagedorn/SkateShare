package com.skateshare.views.feed

import com.skateshare.models.Post

class SleepNightListener(val clickListener: (uid: String) -> Unit) {
    fun onClick(post: Post) = clickListener(post.posterId)
}