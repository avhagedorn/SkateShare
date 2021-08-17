package com.skateshare.views.routes.publicroutesrecyclerview

import androidx.recyclerview.widget.DiffUtil
import com.skateshare.models.RoutePost

class RoutePostDiffCallback : DiffUtil.ItemCallback<RoutePost>() {

    override fun areItemsTheSame(oldItem: RoutePost, newItem: RoutePost): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: RoutePost, newItem: RoutePost): Boolean {
        return oldItem.startLat == newItem.startLat
                && oldItem.startLng == newItem.startLng
                && oldItem.lengthMi == newItem.lengthMi
                && oldItem.terrainType == newItem.terrainType
                && oldItem.boardType == newItem.boardType
                && oldItem.roadType == newItem.roadType
    }

}
