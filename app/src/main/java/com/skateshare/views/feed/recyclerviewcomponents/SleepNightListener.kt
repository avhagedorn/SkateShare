package com.skateshare.views.feed.recyclerviewcomponents

import com.skateshare.models.FeedItem
import com.skateshare.models.Post

class SleepNightListener(val clickListener: (uid: String) -> Unit,
                         val deleteListener: (id: String, pos: Int) -> Unit) {

    fun onClick(item: FeedItem) = clickListener(item.posterId)
    fun onDelete(post: Post, position: Int) = deleteListener(post.id, position)
}