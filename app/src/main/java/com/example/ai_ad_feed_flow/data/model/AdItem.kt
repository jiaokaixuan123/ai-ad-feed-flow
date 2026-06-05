package com.example.ai_ad_feed_flow.data.model

data class AdItem(
    val id: String,
    val channel: FeedChannel,
    val type: AdType,
    val title: String,
    val brand: String,
    val summary: String,
    val tags: List<String>,
    val coverUrl: String,
    val videoUrl: String?,
    val images: List<String> = emptyList(),
    val description: String
)
