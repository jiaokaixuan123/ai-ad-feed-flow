package com.example.ai_ad_feed_flow.data.model

data class InteractionState(
    val liked: Boolean = false,
    val collected: Boolean = false,
    val shareCount: Int = 0,
    val clickCount: Int = 0
)
