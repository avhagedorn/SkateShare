package com.skateshare.views.routes.publicroutesrecyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skateshare.databinding.PublicRouteItemBinding
import com.skateshare.models.RoutePost

class LiteRoutePostViewHolder private constructor(private val binding: PublicRouteItemBinding)
    : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup) : LiteRoutePostViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = PublicRouteItemBinding.inflate(inflater, parent, false)
            return LiteRoutePostViewHolder(binding)
        }
    }

    fun bind(route: RoutePost, unit: String, clickListener: RoutePostListener, avgSpeed: Float) {
        binding.route = route
        binding.unit = unit
        binding.avgSpeed = avgSpeed
        binding.listener = clickListener
        binding.executePendingBindings()
    }
}