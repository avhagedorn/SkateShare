package com.skateshare.views.routes.privaterecyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skateshare.databinding.PrivateRouteItemBinding
import com.skateshare.models.Route

class RouteViewHolder private constructor(private val binding: PrivateRouteItemBinding)
    : SimpleItemViewHolder(binding.root) {

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

        binding.deleteIcon.setOnClickListener {
            clickListener.onDelete(layoutPosition, route)
        }
    }
}