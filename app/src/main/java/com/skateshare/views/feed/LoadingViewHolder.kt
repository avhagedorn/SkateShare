package com.skateshare.views.feed

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
import com.skateshare.R
import com.skateshare.databinding.FeedPostBinding
import com.skateshare.models.Post
import java.util.zip.Inflater

class LoadingViewHolder private constructor(private val itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun from(parent: ViewGroup) : LoadingViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.feed_loading, parent, false)
            return LoadingViewHolder(view)
        }
    }
}