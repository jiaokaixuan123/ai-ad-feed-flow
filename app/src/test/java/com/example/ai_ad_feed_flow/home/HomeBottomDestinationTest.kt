package com.example.ai_ad_feed_flow.home

import com.example.ai_ad_feed_flow.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HomeBottomDestinationTest {
    @Test
    fun exposesWechatStyleBottomDestinationsInOrder() {
        assertEquals(
            listOf(
                HomeBottomDestination.FEED,
                HomeBottomDestination.STATS,
                HomeBottomDestination.SETTINGS
            ),
            HomeBottomDestination.entries.toList()
        )
    }

    @Test
    fun mapsMenuIdsToDestinations() {
        assertEquals(
            HomeBottomDestination.FEED,
            HomeBottomDestination.fromMenuItemId(R.id.menu_home_feed)
        )
        assertEquals(
            HomeBottomDestination.STATS,
            HomeBottomDestination.fromMenuItemId(R.id.menu_home_stats)
        )
        assertEquals(
            HomeBottomDestination.SETTINGS,
            HomeBottomDestination.fromMenuItemId(R.id.menu_home_settings)
        )
    }

    @Test
    fun onlySecondaryDestinationsHavePlaceholderActions() {
        assertNull(HomeBottomDestination.FEED.placeholderAction)
        assertEquals(HomeEntryAction.STATS, HomeBottomDestination.STATS.placeholderAction)
        assertEquals(HomeEntryAction.SETTINGS, HomeBottomDestination.SETTINGS.placeholderAction)
    }
}
