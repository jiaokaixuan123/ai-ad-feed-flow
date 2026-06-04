package com.example.ai_ad_feed_flow.detail

import com.example.ai_ad_feed_flow.data.model.FeedCardUiModel

data class DetailUiState(
    val card: FeedCardUiModel? = null,
    val errorMessage: String? = null
)
