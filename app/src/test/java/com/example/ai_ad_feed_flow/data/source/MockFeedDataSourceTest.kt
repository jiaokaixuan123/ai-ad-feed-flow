package com.example.ai_ad_feed_flow.data.source

import com.example.ai_ad_feed_flow.data.model.AdType
import com.example.ai_ad_feed_flow.data.model.FeedChannel
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MockFeedDataSourceTest {
    private val source = MockFeedDataSource()

    @Test
    fun returnsDifferentItemsForEachChannel() = runTest {
        val featured = source.getPage(FeedChannel.FEATURED, page = 1, pageSize = 3)
        val ecommerce = source.getPage(FeedChannel.ECOMMERCE, page = 1, pageSize = 3)

        assertEquals(3, featured.items.size)
        assertEquals(3, ecommerce.items.size)
        assertTrue(featured.items.all { it.channel == FeedChannel.FEATURED })
        assertTrue(ecommerce.items.all { it.channel == FeedChannel.ECOMMERCE })
        assertNotEquals(featured.items.first().id, ecommerce.items.first().id)
    }

    @Test
    fun paginatesAndReportsHasMore() = runTest {
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

    @Test
    fun containsAllFourAdTypes() = runTest {
        val page = source.getPage(FeedChannel.FEATURED, page = 1, pageSize = 12)
        val types = page.items.map { it.type }.toSet()

        assertTrue(types.contains(AdType.BIG_IMAGE))
        assertTrue(types.contains(AdType.SMALL_IMAGE))
        assertTrue(types.contains(AdType.IMAGE_TEXT))
        assertTrue(types.contains(AdType.VIDEO))
    }

    @Test
    fun imageTextItemsHaveImages() = runTest {
        val page = source.getPage(FeedChannel.ECOMMERCE, page = 1, pageSize = 12)
        val imageTextItems = page.items.filter { it.type == AdType.IMAGE_TEXT }

        assertTrue(imageTextItems.isNotEmpty())
        assertTrue(imageTextItems.all { it.images.isNotEmpty() })
    }

    @Test
    fun getByIdReturnsCorrectItem() = runTest {
        val page = source.getPage(FeedChannel.FEATURED, page = 1, pageSize = 6)
        val targetId = page.items.first().id

        val found = source.getById(targetId)

        assertEquals(targetId, found?.id)
    }
}
