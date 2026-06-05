package com.example.ai_ad_feed_flow.data.source

import com.example.ai_ad_feed_flow.data.model.AdItem
import com.example.ai_ad_feed_flow.data.model.AdType
import com.example.ai_ad_feed_flow.data.model.FeedChannel
import com.example.ai_ad_feed_flow.data.model.PageResult
import com.example.ai_ad_feed_flow.data.network.AdItemDto
import com.example.ai_ad_feed_flow.data.network.FeedApi

class RetrofitFeedDataSource(private val api: FeedApi) : FeedDataSource {

    override suspend fun getPage(
        channel: FeedChannel,
        page: Int,
        pageSize: Int
    ): PageResult<AdItem> {
        val response = api.getFeed(
            channel = channel.apiKey,
            page = page,
            size = pageSize
        )
        return PageResult(
            items = response.items.map { it.toDomain(channel) },
            page = response.page,
            hasMore = response.hasMore
        )
    }

    override suspend fun getById(id: String): AdItem? = null

    private fun AdItemDto.toDomain(channel: FeedChannel): AdItem = AdItem(
        id = id,
        channel = channel,
        type = type.toAdType(),
        title = title,
        brand = brand,
        summary = summary,
        tags = tags,
        coverUrl = coverUrl,
        videoUrl = videoUrl,
        images = images,
        description = description
    )

    private fun String.toAdType(): AdType = when (this) {
        "big_image" -> AdType.BIG_IMAGE
        "small_image" -> AdType.SMALL_IMAGE
        "image_text" -> AdType.IMAGE_TEXT
        "video" -> AdType.VIDEO
        else -> AdType.BIG_IMAGE
    }
}

private val FeedChannel.apiKey: String
    get() = when (this) {
        FeedChannel.FEATURED -> "featured"
        FeedChannel.ECOMMERCE -> "ecom"
        FeedChannel.LOCAL -> "local"
    }
