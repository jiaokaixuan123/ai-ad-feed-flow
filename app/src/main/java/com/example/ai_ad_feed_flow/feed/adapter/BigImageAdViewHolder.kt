package com.example.ai_ad_feed_flow.feed.adapter

import androidx.recyclerview.widget.RecyclerView
import com.example.ai_ad_feed_flow.data.model.FeedCardUiModel
import com.example.ai_ad_feed_flow.databinding.ItemAdBigImageBinding

class BigImageAdViewHolder(
    private val binding: ItemAdBigImageBinding,
    private val onCardClick: (FeedCardUiModel) -> Unit,
    private val onLikeClick: (FeedCardUiModel) -> Unit,
    private val onCollectClick: (FeedCardUiModel) -> Unit,
    private val onShareClick: (FeedCardUiModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(card: FeedCardUiModel) = with(binding) {
        val context = root.context
        coverLabel.text = card.ad.title
        coverLabel.setBackgroundResource(card.ad.channel.coverColorRes())
        titleText.text = card.ad.title
        brandText.text = card.ad.brand
        summaryText.text = card.ad.summary
        tagsText.text = card.tagsLabel()
        statsText.text = card.statsLabel()
        likeButton.text = card.likeLabel(context)
        collectButton.text = card.collectLabel(context)

        root.setOnClickListener { onCardClick(card) }
        likeButton.setOnClickListener { onLikeClick(card) }
        collectButton.setOnClickListener { onCollectClick(card) }
        shareButton.setOnClickListener { onShareClick(card) }
    }
}
