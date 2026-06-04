package com.example.ai_ad_feed_flow.data.repository

import com.example.ai_ad_feed_flow.data.model.FeedChannel
import com.example.ai_ad_feed_flow.data.source.MockFeedDataSource
import com.example.ai_ad_feed_flow.data.store.InteractionStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedRepositoryTest {
    @Test
    fun mergesInteractionStateIntoFeedCards() {
        val store = InteractionStore()
        val repository = FeedRepository(MockFeedDataSource(), store)

        val firstPage = repository.loadPage(FeedChannel.FEATURED, page = 1, pageSize = 3)
        val firstId = firstPage.items.first().ad.id

        store.toggleLike(firstId)
        val refreshed = repository.loadedCards(FeedChannel.FEATURED)

        assertTrue(refreshed.first { it.ad.id == firstId }.interaction.liked)
    }

    @Test
    fun refreshReplacesLoadedCardsForChannel() {
        val repository = FeedRepository(MockFeedDataSource(), InteractionStore())

        repository.loadPage(FeedChannel.ECOMMERCE, page = 1, pageSize = 3)
        repository.loadPage(FeedChannel.ECOMMERCE, page = 2, pageSize = 3)
        val refreshed = repository.loadPage(
            channel = FeedChannel.ECOMMERCE,
            page = 1,
            pageSize = 3,
            refresh = true
        )

        assertEquals(3, refreshed.items.size)
        assertEquals(3, repository.loadedCards(FeedChannel.ECOMMERCE).size)
    }
}
