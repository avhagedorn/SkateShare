package com.skateshare.views.feed

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.skateshare.R
import com.skateshare.models.Post

@BindingAdapter("postUsername")
fun TextView.setPostUsername(item: Post) {
    text = item.posterUsername
}

@BindingAdapter("postDescription")
fun TextView.setPostDescription(item: Post) {
    text = item.description
}

@BindingAdapter("postImage")
fun ImageView.setPostImage(item: Post) {
    Glide.with(context)
         .load(item.imageUrl)
         .into(this)
}
