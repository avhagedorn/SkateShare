package com.skateshare.views.feed.recyclerviewcomponents

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.skateshare.models.Post
import java.text.DateFormat

@BindingAdapter("postUsername")
fun TextView.setPostUsername(item: Post) {
    text = item.posterUsername
}

@BindingAdapter("postDescription")
fun TextView.setPostDescription(item: Post) {
    text = item.description
}

@BindingAdapter("postProfilePicture")
fun ImageView.setProfilePicture(item: Post) {
    Glide.with(context)
         .load(item.postProfilePictureUrl)
         .circleCrop()
         .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
         .into(this)
}

@BindingAdapter("postDate")
fun TextView.setPostDate(item: Post) {
    text = DateFormat.getInstance().format(item.datePosted.toDate())
}

@BindingAdapter("settingsVisibility")
fun ImageView.setSettingsVisibility(item: Post) {
    visibility = if (item.isCurrentUser)
        View.VISIBLE
    else
        View.GONE
}