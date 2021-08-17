package com.skateshare.views.profile

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.skateshare.R
import com.skateshare.misc.UNIT_KILOMETERS
import com.skateshare.misc.UNIT_MILES
import com.skateshare.models.Board

fun setTags(template: String, mphTemplate: String, kphTemplate: String,
                board: Board, units: String) : String {
    var speedString = ""
    speedString = when (units) {
        UNIT_MILES ->
            mphTemplate.format(board.topSpeedMph)
        UNIT_KILOMETERS ->
            kphTemplate.format(board.topSpeedKph)
        else ->
            throw Exception("Invalid units!")
    }
    return template.format(
        board.ampHours,
        board.batteryConfiguration,
        speedString,
        board.motorType,
        board.escType
    )
}
