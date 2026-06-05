package com.example.ai_ad_feed_flow.data.source

import com.example.ai_ad_feed_flow.data.model.AdItem
import com.example.ai_ad_feed_flow.data.model.AdType
import com.example.ai_ad_feed_flow.data.model.FeedChannel
import com.example.ai_ad_feed_flow.data.model.PageResult

class MockFeedDataSource : FeedDataSource {
    private val items: List<AdItem> = FeedChannel.entries.flatMap { channel ->
        (1..12).map { index -> createItem(channel, index) }
    }

    override suspend fun getPage(channel: FeedChannel, page: Int, pageSize: Int): PageResult<AdItem> {
        val channelItems = items.filter { it.channel == channel }
        val safePage = page.coerceAtLeast(1)
        val safePageSize = pageSize.coerceAtLeast(1)
        val fromIndex = (safePage - 1) * safePageSize
        val pageItems = channelItems.drop(fromIndex).take(safePageSize)

        return PageResult(
            items = pageItems,
            page = safePage,
            hasMore = fromIndex + safePageSize < channelItems.size
        )
    }

    override suspend fun getById(id: String): AdItem? {
        return items.firstOrNull { it.id == id }
    }

    private fun createItem(channel: FeedChannel, index: Int): AdItem {
        val type = when (index % 4) {
            1 -> AdType.BIG_IMAGE
            2 -> AdType.SMALL_IMAGE
            3 -> AdType.IMAGE_TEXT
            else -> AdType.VIDEO
        }
        val channelPrefix = when (channel) {
            FeedChannel.FEATURED -> "featured"
            FeedChannel.ECOMMERCE -> "ecom"
            FeedChannel.LOCAL -> "local"
        }
        val titlePrefix = when (channel) {
            FeedChannel.FEATURED -> "精选灵感"
            FeedChannel.ECOMMERCE -> "电商好物"
            FeedChannel.LOCAL -> "本地生活"
        }
        val tags = when (channel) {
            FeedChannel.FEATURED -> listOf("高质感", "年轻人", "通勤")
            FeedChannel.ECOMMERCE -> listOf("性价比", "学生党", "限时")
            FeedChannel.LOCAL -> listOf("附近", "周末", "体验")
        }

        return AdItem(
            id = "${channelPrefix}_ad_${index.toString().padStart(2, '0')}",
            channel = channel,
            type = type,
            title = "$titlePrefix $index",
            brand = brandFor(channel, index),
            summary = "${channel.title}频道 AI 摘要：这条广告适合关注${tags.first()}和${tags.last()}的人群。",
            tags = tags,
            coverUrl = "mock://$channelPrefix/cover_$index",
            videoUrl = if (type == AdType.VIDEO) "mock://$channelPrefix/video_$index.mp4" else null,
            images = if (type == AdType.IMAGE_TEXT) listOf(
                "mock://$channelPrefix/img_${index}_1",
                "mock://$channelPrefix/img_${index}_2"
            ) else emptyList(),
            description = "这是$titlePrefix $index 的详情说明。MVP 阶段使用本地 mock 数据，后续可以通过 Retrofit 替换为后端分页接口返回的数据。"
        )
    }

    private fun brandFor(channel: FeedChannel, index: Int): String {
        return when (channel) {
            FeedChannel.FEATURED -> "Flow Studio ${index % 4 + 1}"
            FeedChannel.ECOMMERCE -> "AIFeed Mart ${index % 4 + 1}"
            FeedChannel.LOCAL -> "City Picks ${index % 4 + 1}"
        }
    }
}
