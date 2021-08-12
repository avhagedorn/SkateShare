package com.skateshare.views.feed.recyclerviewcomponents

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerCrop
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.skateshare.databinding.FeedPostBinding
import com.skateshare.databinding.RoutePostBinding
import com.skateshare.models.Post
import com.skateshare.models.RoutePost

class RoutePostViewHolder private constructor(private val binding: RoutePostBinding) : ItemViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup) : RoutePostViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = RoutePostBinding.inflate(inflater, parent, false)
            return RoutePostViewHolder(binding)
        }
    }

    fun bind(route: RoutePost, units: String, userAvgSpeed: Float, clickListener: SleepNightListener) {
        binding.route = route
        binding.unit = units
        binding.avgSpeed = userAvgSpeed
        binding.listener = clickListener
        binding.executePendingBindings()

        if (route.imgUrl != null) {
            val postImage = binding.postImage
            Glide.with(postImage.context)
                .load(route.imgUrl)
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
        } else {
            binding.loading.visibility = View.GONE
        }

        binding.deleteIcon.setOnClickListener {
            clickListener.deleteListener(route.id, layoutPosition)
        }
    }
}