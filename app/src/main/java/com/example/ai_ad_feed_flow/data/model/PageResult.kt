package com.example.ai_ad_feed_flow.data.model

data class PageResult<T>(
    val items: List<T>,
    val page: Int,
    val hasMore: Boolean
)
