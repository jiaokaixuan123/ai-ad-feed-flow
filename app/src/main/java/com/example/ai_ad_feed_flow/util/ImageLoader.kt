package com.example.ai_ad_feed_flow.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.ai_ad_feed_flow.R

object ImageLoader {

    private val defaultOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .placeholder(R.color.cover_placeholder)
        .error(R.color.cover_placeholder)

    fun load(imageView: ImageView, url: String?) {
        Glide.with(imageView)
            .load(url)
            .apply(defaultOptions)
            .into(imageView)
    }

    fun loadCover(imageView: ImageView, url: String?) {
        Glide.with(imageView)
            .load(url)
            .apply(defaultOptions.centerCrop())
            .into(imageView)
    }

    fun loadFitCenter(imageView: ImageView, url: String?) {
        Glide.with(imageView)
            .load(url)
            .apply(defaultOptions.fitCenter())
            .into(imageView)
    }
}
