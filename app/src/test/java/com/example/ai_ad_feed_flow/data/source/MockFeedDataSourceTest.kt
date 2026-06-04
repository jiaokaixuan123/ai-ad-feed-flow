package com.example.ai_ad_feed_flow.data.source

import com.example.ai_ad_feed_flow.data.model.FeedChannel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MockFeedDataSourceTest {
    private val source = MockFeedDataSource()

    @Test
    fun returnsDifferentItemsForEachChannel() {
        val featured = source.getPage(FeedChannel.FEATURED, page = 1, pageSize = 3)
        val ecommerce = source.getPage(FeedChannel.ECOMMERCE, page = 1, pageSize = 3)

        assertEquals(3, featured.items.size)
        assertEquals(3, ecommerce.items.size)
        assertTrue(featured.items.all { it.channel == FeedChannel.FEATURED })
        assertTrue(ecommerce.items.all { it.channel == FeedChannel.ECOMMERCE })
        assertNotEquals(featured.items.first().id, ecommerce.items.first().id)
    }

    @Test
    fun paginatesAndReportsHasMore() {
        val first = source.getPage(FeedChannel.LOCAL, page = 1, pageSize = 4)
        val second = source.getPage(FeedChannel.LOCAL, page = 2, pageSize = 4)
        val finalPage = source.getPage(FeedChannel.LOCAL, page = 3, pageSize = 4)

        assertEquals(4, first.items.size)
        assertTrue(first.hasMore)
        assertEquals(4, second.items.size)
        assertNotEquals(first.items.first().id, second.items.first().id)
        assertEquals(4, finalPage.items.size)
        assertTrue(!finalPage.hasMore)
    }
}
