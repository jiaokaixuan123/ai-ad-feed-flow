package com.example.ai_ad_feed_flow

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ai_ad_feed_flow.data.model.FeedChannel
import com.example.ai_ad_feed_flow.databinding.ActivityMainBinding
import com.example.ai_ad_feed_flow.feed.FeedPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        binding.feedPager.adapter = FeedPagerAdapter(this)

        TabLayoutMediator(binding.channelTabs, binding.feedPager) { tab, position ->
            tab.text = FeedChannel.entries[position].title
        }.attach()
    }
}
