package com.example.ai_ad_feed_flow

import com.example.ai_ad_feed_flow.data.repository.FeedRepository
import com.example.ai_ad_feed_flow.data.source.MockFeedDataSource
import com.example.ai_ad_feed_flow.data.store.InteractionStore

object AppGraph {
    private val interactionStore = InteractionStore()

    val feedRepository: FeedRepository = FeedRepository(
        dataSource = MockFeedDataSource(),
        interactionStore = interactionStore
    )
}
