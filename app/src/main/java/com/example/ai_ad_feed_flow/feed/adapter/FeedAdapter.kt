package com.example.ai_ad_feed_flow.feed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_ad_feed_flow.data.model.AdType
import com.example.ai_ad_feed_flow.data.model.FeedCardUiModel
import com.example.ai_ad_feed_flow.databinding.ItemAdBigImageBinding
import com.example.ai_ad_feed_flow.databinding.ItemAdSmallImageBinding
import com.example.ai_ad_feed_flow.databinding.ItemAdVideoBinding

class FeedAdapter(
    private val onCardClick: (FeedCardUiModel) -> Unit,
    private val onLikeClick: (FeedCardUiModel) -> Unit,
    private val onCollectClick: (FeedCardUiModel) -> Unit,
    private val onShareClick: (FeedCardUiModel) -> Unit
) : ListAdapter<FeedCardUiModel, RecyclerView.ViewHolder>(DiffCallback) {
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).ad.type) {
            AdType.BIG_IMAGE -> VIEW_TYPE_BIG_IMAGE
            AdType.SMALL_IMAGE -> VIEW_TYPE_SMALL_IMAGE
            AdType.VIDEO -> VIEW_TYPE_VIDEO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_BIG_IMAGE -> BigImageAdViewHolder(
                binding = ItemAdBigImageBinding.inflate(inflater, parent, false),
                onCardClick = onCardClick,
                onLikeClick = onLikeClick,
                onCollectClick = onCollectClick,
                onShareClick = onShareClick
            )
            VIEW_TYPE_SMALL_IMAGE -> SmallImageAdViewHolder(
                binding = ItemAdSmallImageBinding.inflate(inflater, parent, false),
                onCardClick = onCardClick,
                onLikeClick = onLikeClick,
                onCollectClick = onCollectClick,
                onShareClick = onShareClick
            )
            else -> VideoAdViewHolder(
                binding = ItemAdVideoBinding.inflate(inflater, parent, false),
                onCardClick = onCardClick,
                onLikeClick = onLikeClick,
                onCollectClick = onCollectClick,
                onShareClick = onShareClick
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val card = getItem(position)
        when (holder) {
            is BigImageAdViewHolder -> holder.bind(card)
            is SmallImageAdViewHolder -> holder.bind(card)
            is VideoAdViewHolder -> holder.bind(card)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<FeedCardUiModel>() {
        override fun areItemsTheSame(oldItem: FeedCardUiModel, newItem: FeedCardUiModel): Boolean {
            return oldItem.ad.id == newItem.ad.id
        }

        override fun areContentsTheSame(oldItem: FeedCardUiModel, newItem: FeedCardUiModel): Boolean {
            return oldItem == newItem
        }
    }

    private companion object {
        const val VIEW_TYPE_BIG_IMAGE = 1
        const val VIEW_TYPE_SMALL_IMAGE = 2
        const val VIEW_TYPE_VIDEO = 3
    }
}
