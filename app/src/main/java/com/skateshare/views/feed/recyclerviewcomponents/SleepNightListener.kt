package com.skateshare.views.feed.recyclerviewcomponents

import android.view.View
import com.skateshare.models.Post

class SleepNightListener(val clickListener: (uid: String) -> Unit,
                         val deleteListener: (id: String, pos: Int) -> Unit) {

    fun onClick(post: Post) = clickListener(post.posterId)
    fun onDelete(post: Post, position: Int) = deleteListener(post.id, position)
}