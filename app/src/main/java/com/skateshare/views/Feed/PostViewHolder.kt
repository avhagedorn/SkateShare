package com.skateshare.views.Feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skateshare.R
import com.skateshare.models.Post

class PostViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

    companion object {
        fun from(parent: ViewGroup) : PostViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.feed_post, parent, false)
            return PostViewHolder(view)
        }
    }

    private val postPicture: ImageView = itemView.findViewById(R.id.post_image)
    private val postUsername: TextView = itemView.findViewById(R.id.post_username)
    private val postDescription: TextView = itemView.findViewById(R.id.post_description)

    fun bind(post: Post) {
        // TODO: CHECK IF CONTEXT IS CORRECT
        Glide.with(itemView.context)
             .load(post.imageUrl)
             .into(postPicture)
        postUsername.text = post.posterUsername
        postDescription.text = post.description
    }
}