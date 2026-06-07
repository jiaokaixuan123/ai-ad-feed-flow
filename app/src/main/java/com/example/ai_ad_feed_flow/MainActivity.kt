package com.example.ai_ad_feed_flow

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.ai_ad_feed_flow.data.model.FeedChannel
import com.example.ai_ad_feed_flow.databinding.ActivityMainBinding
import com.example.ai_ad_feed_flow.feed.FeedPagerAdapter
import com.example.ai_ad_feed_flow.home.HomeBottomDestination
import com.example.ai_ad_feed_flow.home.HomeEntryAction
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.feed_top_bar)
        WindowInsetsControllerCompat(window, binding.main).isAppearanceLightStatusBars = true

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupHomeEntries()
        binding.feedPager.adapter = FeedPagerAdapter(this)

        TabLayoutMediator(binding.channelTabs, binding.feedPager) { tab, position ->
            tab.text = FeedChannel.entries[position].title
        }.attach()
    }

    private fun setupHomeEntries() {
        binding.topMenuButton.setOnClickListener {
            showEntryPlaceholder(HomeEntryAction.MENU)
        }
        binding.topSearchButton.setOnClickListener {
            showEntryPlaceholder(HomeEntryAction.SEARCH)
        }

        binding.bottomNavigation.selectedItemId = R.id.menu_home_feed
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val destination = HomeBottomDestination.fromMenuItemId(item.itemId)
                ?: return@setOnItemSelectedListener false
            destination.placeholderAction?.let(::showEntryPlaceholder)
            binding.bottomNavigation.post {
                binding.bottomNavigation.selectedItemId = R.id.menu_home_feed
            }
            destination == HomeBottomDestination.FEED
        }
    }

    private fun showEntryPlaceholder(action: HomeEntryAction) {
        Toast.makeText(this, action.toastMessageRes, Toast.LENGTH_SHORT).show()
    }
}
