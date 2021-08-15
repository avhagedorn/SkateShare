package com.skateshare.views.routes.publicrecyclerviewcomponents

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.skateshare.models.RoutePost

class PublicRoutesAdapter (val listener: RoutePostListener, val unit: String, val avgSpeed: Float)
    : ListAdapter<RoutePost, LiteRoutePostViewHolder>(RoutePostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiteRoutePostViewHolder {
        return LiteRoutePostViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: LiteRoutePostViewHolder, position: Int) {
        holder.bind(getItem(position), unit, listener, avgSpeed)
    }

}
