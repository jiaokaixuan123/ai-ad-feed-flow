package com.example.ai_ad_feed_flow.data.source

import com.example.ai_ad_feed_flow.data.model.AdItem
import com.example.ai_ad_feed_flow.data.model.FeedChannel
import com.example.ai_ad_feed_flow.data.model.PageResult

interface FeedDataSource {
    suspend fun getPage(channel: FeedChannel, page: Int, pageSize: Int): PageResult<AdItem>

    suspend fun getById(id: String): AdItem?
}
