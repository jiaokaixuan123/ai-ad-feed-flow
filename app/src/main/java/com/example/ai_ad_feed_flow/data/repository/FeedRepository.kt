package com.example.ai_ad_feed_flow.data.repository

import com.example.ai_ad_feed_flow.data.model.AdItem
import com.example.ai_ad_feed_flow.data.model.FeedCardUiModel
import com.example.ai_ad_feed_flow.data.model.FeedChannel
import com.example.ai_ad_feed_flow.data.model.InteractionState
import com.example.ai_ad_feed_flow.data.model.PageResult
import com.example.ai_ad_feed_flow.data.source.FeedDataSource
import com.example.ai_ad_feed_flow.data.store.InteractionStore
import kotlinx.coroutines.flow.StateFlow

class FeedRepository(
    private val dataSource: FeedDataSource,
    private val interactionStore: InteractionStore
) {
    private val loadedItemsByChannel = mutableMapOf<FeedChannel, MutableList<AdItem>>()

    val interactionStates: StateFlow<Map<String, InteractionState>> = interactionStore.states

    fun loadPage(
        channel: FeedChannel,
        page: Int,
        pageSize: Int,
        refresh: Boolean = false
    ): PageResult<FeedCardUiModel> {
        val result = dataSource.getPage(channel, page, pageSize)
        val loadedItems = if (refresh || page == FIRST_PAGE) {
            mutableListOf()
        } else {
            loadedItemsByChannel[channel]?.toMutableList() ?: mutableListOf()
        }

        loadedItems.addAll(result.items)
        loadedItemsByChannel[channel] = loadedItems

        return PageResult(
            items = loadedItems.map(::toCard),
            page = result.page,
            hasMore = result.hasMore
        )
    }

    fun loadedCards(channel: FeedChannel): List<FeedCardUiModel> {
        return loadedItemsByChannel[channel].orEmpty().map(::toCard)
    }

    fun getCard(id: String): FeedCardUiModel? {
        return dataSource.getById(id)?.let(::toCard)
    }

    fun toggleLike(id: String) {
        interactionStore.toggleLike(id)
    }

    fun toggleCollect(id: String) {
        interactionStore.toggleCollect(id)
    }

    fun share(id: String) {
        interactionStore.share(id)
    }

    fun recordClick(id: String) {
        interactionStore.recordClick(id)
    }

    private fun toCard(adItem: AdItem): FeedCardUiModel {
        return FeedCardUiModel(
            ad = adItem,
            interaction = interactionStore.currentState(adItem.id)
        )
    }

    private companion object {
        const val FIRST_PAGE = 1
    }
}
