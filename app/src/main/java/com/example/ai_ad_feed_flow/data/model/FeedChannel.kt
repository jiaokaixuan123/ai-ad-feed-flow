package com.example.ai_ad_feed_flow.data.model

enum class FeedChannel(val title: String) {
    FEATURED("精选"),
    ECOMMERCE("电商"),
    LOCAL("本地");

    companion object {
        fun fromName(name: String?): FeedChannel {
            return entries.firstOrNull { it.name == name } ?: FEATURED
        }
    }
}
