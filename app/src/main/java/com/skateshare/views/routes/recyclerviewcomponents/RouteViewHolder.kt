package com.skateshare.views.routes.recyclerviewcomponents

import com.skateshare.views.feed.recyclerviewcomponents.ItemViewHolder
import com.skateshare.views.feed.recyclerviewcomponents.SleepNightListener
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
import com.skateshare.databinding.PrivateRouteItemBinding
import com.skateshare.misc.UNIT_MILES
import com.skateshare.models.Post
import com.skateshare.models.Route

class RouteViewHolder private constructor(private val binding: PrivateRouteItemBinding)
    : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup) : RouteViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = PrivateRouteItemBinding.inflate(inflater, parent, false)
            return RouteViewHolder(binding)
        }
    }

    fun bind(route: Route, unit: String, clickListener: RouteListener) {
        binding.route = route
        binding.unit = unit
        binding.listener = clickListener
        binding.executePendingBindings()
    }
}