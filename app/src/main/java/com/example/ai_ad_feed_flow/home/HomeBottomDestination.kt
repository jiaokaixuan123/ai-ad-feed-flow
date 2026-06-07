package com.example.ai_ad_feed_flow.home

import androidx.annotation.IdRes
import com.example.ai_ad_feed_flow.R

enum class HomeBottomDestination(
    @get:IdRes val menuItemId: Int,
    val placeholderAction: HomeEntryAction?
) {
    FEED(R.id.menu_home_feed, null),
    STATS(R.id.menu_home_stats, HomeEntryAction.STATS),
    SETTINGS(R.id.menu_home_settings, HomeEntryAction.SETTINGS);

    companion object {
        fun fromMenuItemId(@IdRes menuItemId: Int): HomeBottomDestination? {
            return entries.firstOrNull { it.menuItemId == menuItemId }
        }
    }
}
