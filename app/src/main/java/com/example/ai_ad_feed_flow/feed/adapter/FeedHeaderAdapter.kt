package com.example.ai_ad_feed_flow.feed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_ad_feed_flow.databinding.ViewFeedHeaderBinding
import com.example.ai_ad_feed_flow.home.HomeEntryAction

class FeedHeaderAdapter(
    private val onEntryClick: (HomeEntryAction) -> Unit
) : RecyclerView.Adapter<FeedHeaderAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ViewFeedHeaderBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
    }

    override fun getItemCount(): Int = HEADER_ITEM_COUNT

    inner class ViewHolder(
        private val binding: ViewFeedHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() = with(binding) {
            searchEntry.setOnClickListener {
                onEntryClick(HomeEntryAction.SEARCH)
            }
            interactionsEntry.setOnClickListener {
                onEntryClick(HomeEntryAction.INTERACTIONS)
            }
        }
    }

    private companion object {
        const val HEADER_ITEM_COUNT = 1
    }
}
