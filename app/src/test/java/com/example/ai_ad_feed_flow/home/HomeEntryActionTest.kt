package com.example.ai_ad_feed_flow.home

import com.example.ai_ad_feed_flow.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeEntryActionTest {
    @Test
    fun mapsSearchEntryToSearchToast() {
        assertEquals(
            R.string.home_entry_search_unavailable,
            HomeEntryAction.SEARCH.toastMessageRes
        )
    }

    @Test
    fun mapsMenuEntryToMenuToast() {
        assertEquals(
            R.string.home_entry_menu_unavailable,
            HomeEntryAction.MENU.toastMessageRes
        )
    }

    @Test
    fun mapsStatsEntryToStatsToast() {
        assertEquals(
            R.string.home_entry_stats_unavailable,
            HomeEntryAction.STATS.toastMessageRes
        )
    }

    @Test
    fun mapsInteractionsEntryToInteractionsToast() {
        assertEquals(
            R.string.home_entry_interactions_unavailable,
            HomeEntryAction.INTERACTIONS.toastMessageRes
        )
    }

    @Test
    fun mapsSettingsEntryToSettingsToast() {
        assertEquals(
            R.string.home_entry_settings_unavailable,
            HomeEntryAction.SETTINGS.toastMessageRes
        )
    }

    @Test
    fun marksStatsAndSettingsAsBottomSwitchActions() {
        assertFalse(HomeEntryAction.SEARCH.isBottomSwitchAction)
        assertFalse(HomeEntryAction.MENU.isBottomSwitchAction)
        assertTrue(HomeEntryAction.STATS.isBottomSwitchAction)
        assertFalse(HomeEntryAction.INTERACTIONS.isBottomSwitchAction)
        assertTrue(HomeEntryAction.SETTINGS.isBottomSwitchAction)
    }
}
