package com.example.ai_ad_feed_flow.data.repository

import com.example.ai_ad_feed_flow.data.model.FeedChannel
import com.example.ai_ad_feed_flow.data.source.MockFeedDataSource
import com.example.ai_ad_feed_flow.data.store.InteractionStore
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedRepositoryTest {
    @Test
    fun mergesInteractionStateIntoFeedCards() = runTest {
        val store = InteractionStore()
        val repository = FeedRepository(MockFeedDataSource(), store)

        val firstPage = repository.loadPage(FeedChannel.FEATURED, page = 1, pageSize = 3)
        val firstId = firstPage.items.first().ad.id

        store.toggleLike(firstId)
        val refreshed = repository.loadedCards(FeedChannel.FEATURED)

        assertTrue(refreshed.first { it.ad.id == firstId }.interaction.liked)
    }

    @Test
    fun refreshReplacesLoadedCardsForChannel() = runTest {
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

    @Test
    fun recordImpressionUpdatesState() = runTest {
        val store = InteractionStore()
        val repository = FeedRepository(MockFeedDataSource(), store)

        val firstPage = repository.loadPage(FeedChannel.FEATURED, page = 1, pageSize = 3)
        val adId = firstPage.items.first().ad.id

        repository.recordImpression(adId)
        repository.recordImpression(adId)

        assertEquals(2, store.currentState(adId).impressionCount)
    }

    @Test
    fun getCardReturnsMergedCardById() = runTest {
        val store = InteractionStore()
        val repository = FeedRepository(MockFeedDataSource(), store)

        val firstPage = repository.loadPage(FeedChannel.FEATURED, page = 1, pageSize = 3)
        val adId = firstPage.items.first().ad.id

        store.toggleLike(adId)
        val card = repository.getCard(adId)

        assertEquals(adId, card?.ad?.id)
        assertTrue(card?.interaction?.liked == true)
    }

    @Test
    fun loadNextPageAppendsToPreviousPage() = runTest {
        val repository = FeedRepository(MockFeedDataSource(), InteractionStore())

        val page1 = repository.loadPage(FeedChannel.LOCAL, page = 1, pageSize = 4)
        val page2 = repository.loadPage(FeedChannel.LOCAL, page = 2, pageSize = 4)

        assertEquals(4, page1.items.size)
        assertEquals(8, page2.items.size)
    }
}
