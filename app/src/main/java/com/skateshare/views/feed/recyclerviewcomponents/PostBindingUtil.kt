package com.skateshare.views.feed.recyclerviewcomponents

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.skateshare.R
import com.skateshare.models.FeedItem
import com.skateshare.models.Post
import com.skateshare.models.RoutePost
import java.text.DateFormat

@BindingAdapter("itemUsername")
fun TextView.setPostUsername(item: FeedItem) {
    text = item.posterUsername
}

@BindingAdapter("itemDescription")
fun TextView.setPostDescription(item: FeedItem) {
    text = item.description
}

@BindingAdapter("itemProfilePicture")
fun ImageView.setProfilePicture(item: FeedItem) {
    Glide.with(context)
         .load(item.postProfilePictureUrl)
         .circleCrop()
         .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
         .into(this)
}

@BindingAdapter("itemDate")
fun TextView.setPostDate(item: FeedItem) {
    text = DateFormat.getInstance().format(item.datePosted.toDate())
}

@BindingAdapter("settingsVisibility")
fun ImageView.setSettingsVisibility(item: FeedItem) {
    visibility = if (item.isCurrentUser)
        View.VISIBLE
    else
        View.GONE
}

@BindingAdapter("routeHeader")
fun TextView.setRouteHeader(route: RoutePost) {
    text = context.getString(R.string.route_header, route.posterUsername)
}

@BindingAdapter("routeDescription")
fun TextView.setRouteDescription(route: RoutePost) {
    text = "\uD83E\uDD19 2.3 Miles \n ⚡ Mountainboards recommended! \n \uD83C\uDF04 Fairly Hilly \n \uD83D\uDD25 Your expected time: 36 minutes \n \uD83C\uDF0E Minneapolis, Minnesota, USA"
}
