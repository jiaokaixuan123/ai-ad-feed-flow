package com.example.ai_ad_feed_flow.feed

import com.example.ai_ad_feed_flow.MainDispatcherRule
import com.example.ai_ad_feed_flow.data.model.FeedChannel
import com.example.ai_ad_feed_flow.data.repository.FeedRepository
import com.example.ai_ad_feed_flow.data.source.MockFeedDataSource
import com.example.ai_ad_feed_flow.data.store.InteractionStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun loadFirstPageUpdatesItems() = runTest {
        val repository = FeedRepository(MockFeedDataSource(), InteractionStore())
        val viewModel = FeedViewModel(FeedChannel.FEATURED, repository)

        viewModel.loadFirstPage()

        assertEquals(6, viewModel.uiState.value.items.size)
        assertFalse(viewModel.uiState.value.isRefreshing)
        assertTrue(viewModel.uiState.value.hasMore)
    }

    @Test
    fun loadNextPageAppendsItems() = runTest {
        val repository = FeedRepository(MockFeedDataSource(), InteractionStore())
        val viewModel = FeedViewModel(FeedChannel.LOCAL, repository)

        viewModel.loadFirstPage()
        viewModel.loadNextPage()

        assertEquals(12, viewModel.uiState.value.items.size)
        assertFalse(viewModel.uiState.value.hasMore)
    }
}
