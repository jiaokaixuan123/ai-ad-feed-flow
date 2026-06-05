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

    @Test
    fun recordsImpressionCount() {
        val store = InteractionStore()

        store.recordImpression("ad_1")
        store.recordImpression("ad_1")
        store.recordImpression("ad_2")

        assertEquals(2, store.currentState("ad_1").impressionCount)
        assertEquals(1, store.currentState("ad_2").impressionCount)
        assertEquals(0, store.currentState("ad_3").impressionCount)
    }

    @Test
    fun togglingLikeTwiceRestoresOriginalState() {
        val store = InteractionStore()

        store.toggleLike("ad_1")
        store.toggleLike("ad_1")

        assertFalse(store.currentState("ad_1").liked)
    }

    @Test
    fun stateFlowValueReflectsLatestChanges() {
        val store = InteractionStore()

        store.toggleLike("ad_1")
        store.recordClick("ad_1")

        val state = store.states.value["ad_1"]
        assertTrue(state?.liked == true)
        assertEquals(1, state?.clickCount)
    }
}
