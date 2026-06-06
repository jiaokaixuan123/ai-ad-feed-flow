package com.example.ai_ad_feed_flow.feed

import com.example.ai_ad_feed_flow.data.model.AdItem
import com.example.ai_ad_feed_flow.data.model.AdType
import com.example.ai_ad_feed_flow.data.model.FeedCardUiModel
import com.example.ai_ad_feed_flow.data.model.FeedChannel
import com.example.ai_ad_feed_flow.data.model.InteractionState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedStatusVisibilityTest {
    @Test
    fun showsLoadingMoreFooterWhenLoadingMore() {
        val visibility = FeedUiState(
            items = listOf(feedCard()),
            isLoadingMore = true
        ).toFeedStatusVisibility()

        assertTrue(visibility.showLoadingMore)
        assertFalse(visibility.showEmpty)
        assertFalse(visibility.showError)
    }

    @Test
    fun showsEmptyStateWhenListIsEmptyAndNotRefreshing() {
        val visibility = FeedUiState(
            items = emptyList(),
            isRefreshing = false
        ).toFeedStatusVisibility()

        assertFalse(visibility.showLoadingMore)
        assertTrue(visibility.showEmpty)
        assertFalse(visibility.showError)
    }

    @Test
    fun showsErrorStateInsteadOfEmptyStateWhenErrorExists() {
        val visibility = FeedUiState(
            items = emptyList(),
            isRefreshing = false,
            errorMessage = "Load failed"
        ).toFeedStatusVisibility()

        assertFalse(visibility.showLoadingMore)
        assertFalse(visibility.showEmpty)
        assertTrue(visibility.showError)
    }

    private fun feedCard(): FeedCardUiModel {
        return FeedCardUiModel(
            ad = AdItem(
                id = "ad_test",
                channel = FeedChannel.FEATURED,
                type = AdType.BIG_IMAGE,
                title = "Test Ad",
                brand = "Test Brand",
                summary = "AI summary",
                tags = listOf("test"),
                coverUrl = "",
                videoUrl = null,
                description = "Test description"
            ),
            interaction = InteractionState()
        )
    }
}
