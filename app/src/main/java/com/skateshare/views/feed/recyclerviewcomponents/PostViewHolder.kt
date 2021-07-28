package com.skateshare.views.feed.recyclerviewcomponents

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.skateshare.databinding.FeedPostBinding
import com.skateshare.models.Post

class PostViewHolder private constructor(private val binding: FeedPostBinding)
    : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup) : PostViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = FeedPostBinding.inflate(inflater, parent, false)
            return PostViewHolder(binding)
        }
    }

    fun bind(post: Post, clickListener: SleepNightListener) {
        binding.post = post
        binding.listener = clickListener
        binding.executePendingBindings()
        val postImage = binding.postImage

        Glide.with(postImage.context)
            .load(post.imageUrl)
            .priority(Priority.HIGH)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?, target: Target<Drawable>?,
                    isFirstResource: Boolean) = false

                override fun onResourceReady(
                    resource: Drawable?, model: Any?, target: Target<Drawable>?,
                    dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    binding.loading.visibility = View.GONE
                    return false
                }
            }).into(postImage)
    }
}