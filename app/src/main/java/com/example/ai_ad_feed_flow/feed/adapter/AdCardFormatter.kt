package com.example.ai_ad_feed_flow.feed.adapter

import android.content.Context
import com.example.ai_ad_feed_flow.R
import com.example.ai_ad_feed_flow.data.model.FeedCardUiModel
import com.example.ai_ad_feed_flow.data.model.FeedChannel

internal fun FeedCardUiModel.tagsLabel(): String {
    return ad.tags.joinToString(separator = " ") { "#$it" }
}

internal fun FeedCardUiModel.statsLabel(): String {
    return "点击 ${interaction.clickCount} · 分享 ${interaction.shareCount}"
}

internal fun FeedCardUiModel.likeLabel(context: Context): String {
    return context.getString(if (interaction.liked) R.string.action_liked else R.string.action_like)
}

internal fun FeedCardUiModel.collectLabel(context: Context): String {
    return context.getString(
        if (interaction.collected) R.string.action_collected else R.string.action_collect
    )
}

internal fun FeedChannel.coverColorRes(): Int {
    return when (this) {
        FeedChannel.FEATURED -> R.color.feed_cover_featured
        FeedChannel.ECOMMERCE -> R.color.feed_cover_ecommerce
        FeedChannel.LOCAL -> R.color.feed_cover_local
    }
}
