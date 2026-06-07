package com.example.ai_ad_feed_flow.home

import androidx.annotation.StringRes
import com.example.ai_ad_feed_flow.R

enum class HomeEntryAction(@get:StringRes val toastMessageRes: Int) {
    SEARCH(R.string.home_entry_search_unavailable),
    MENU(R.string.home_entry_menu_unavailable),
    STATS(R.string.home_entry_stats_unavailable),
    INTERACTIONS(R.string.home_entry_interactions_unavailable),
    SETTINGS(R.string.home_entry_settings_unavailable);

    val isBottomSwitchAction: Boolean
        get() = this == STATS || this == SETTINGS
}
