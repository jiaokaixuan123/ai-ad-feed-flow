package com.example.ai_ad_feed_flow.feed

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ai_ad_feed_flow.data.model.FeedChannel

class FeedPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    private val channels = FeedChannel.entries.toList()

    override fun getItemCount(): Int = channels.size

    override fun createFragment(position: Int): Fragment {
        return FeedFragment.newInstance(channels[position])
    }
}
