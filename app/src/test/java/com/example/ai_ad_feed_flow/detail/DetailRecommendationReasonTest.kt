package com.example.ai_ad_feed_flow.detail

import com.example.ai_ad_feed_flow.data.model.AdItem
import com.example.ai_ad_feed_flow.data.model.AdType
import com.example.ai_ad_feed_flow.data.model.FeedCardUiModel
import com.example.ai_ad_feed_flow.data.model.FeedChannel
import com.example.ai_ad_feed_flow.data.model.InteractionState
import org.junit.Assert.assertEquals
import org.junit.Test

class DetailRecommendationReasonTest {
    @Test
    fun recommendationBasisUsesChannelTagsAndBrand() {
        val basis = feedCard().recommendationReasonBasis()

        assertEquals(FeedChannel.ECOMMERCE.title, basis.channelTitle)
        assertEquals("commute, value", basis.tagSummary)
        assertEquals("Sonic Lab", basis.brand)
    }

    private fun feedCard(): FeedCardUiModel {
        return FeedCardUiModel(
            ad = AdItem(
                id = "ad_detail_test",
                channel = FeedChannel.ECOMMERCE,
                type = AdType.SMALL_IMAGE,
                title = "Daily headphones",
                brand = "Sonic Lab",
                summary = "AI summary",
                tags = listOf("commute", "value", "student"),
                coverUrl = "",
                videoUrl = null,
                description = "Test description"
            ),
            interaction = InteractionState()
        )
    }
}
