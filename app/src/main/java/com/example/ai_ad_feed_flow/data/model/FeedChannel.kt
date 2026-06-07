package com.example.ai_ad_feed_flow.data.model

enum class FeedChannel(val title: String) {
    FEATURED("推荐"),
    ECOMMERCE("商城"),
    LOCAL("同城");

    companion object {
        fun fromName(name: String?): FeedChannel {
            return entries.firstOrNull { it.name == name } ?: FEATURED
        }
    }
}
