package com.example.ai_ad_feed_flow.data.store

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InteractionStoreTest {
    @Test
    fun togglesLikeAndCollectIndependentlyByAdId() {
        val store = InteractionStore()

        store.toggleLike("ad_1")
        store.toggleCollect("ad_2")

        assertTrue(store.currentState("ad_1").liked)
        assertFalse(store.currentState("ad_1").collected)
        assertFalse(store.currentState("ad_2").liked)
        assertTrue(store.currentState("ad_2").collected)
    }

    @Test
    fun incrementsShareAndClickCounts() {
        val store = InteractionStore()

        store.share("ad_1")
        store.recordClick("ad_1")
        store.recordClick("ad_1")

        assertEquals(1, store.currentState("ad_1").shareCount)
        assertEquals(2, store.currentState("ad_1").clickCount)
    }
}
