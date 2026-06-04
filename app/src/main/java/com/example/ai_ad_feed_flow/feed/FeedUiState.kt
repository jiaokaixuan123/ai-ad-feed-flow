package com.example.ai_ad_feed_flow.feed

import com.example.ai_ad_feed_flow.data.model.FeedCardUiModel

data class FeedUiState(
    val items: List<FeedCardUiModel> = emptyList(),
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = true,
    val errorMessage: String? = null
)
