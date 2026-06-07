package com.example.ai_ad_feed_flow.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.ai_ad_feed_flow.AppGraph
import com.example.ai_ad_feed_flow.R
import com.example.ai_ad_feed_flow.data.model.FeedCardUiModel
import com.example.ai_ad_feed_flow.databinding.ActivityDetailBinding
import com.example.ai_ad_feed_flow.feed.adapter.collectLabel
import com.example.ai_ad_feed_flow.feed.adapter.coverColorRes
import com.example.ai_ad_feed_flow.feed.adapter.likeLabel
import com.example.ai_ad_feed_flow.feed.adapter.statsLabel
import com.example.ai_ad_feed_flow.feed.adapter.tagsLabel
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private val viewModel: DetailViewModel by viewModels {
        DetailViewModel.Factory(
            adId = intent.getStringExtra(EXTRA_AD_ID).orEmpty(),
            repository = AppGraph.feedRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.feed_surface)
        WindowInsetsControllerCompat(window, binding.detailRoot).isAppearanceLightStatusBars = true

        ViewCompat.setOnApplyWindowInsetsListener(binding.detailRoot) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            binding.detailAppBar.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        setSupportActionBar(binding.detailToolbar)
        binding.detailToolbar.setNavigationOnClickListener { finish() }

        binding.likeButton.setOnClickListener { viewModel.toggleLike() }
        binding.collectButton.setOnClickListener { viewModel.toggleCollect() }
        binding.shareButton.setOnClickListener { viewModel.share() }
        binding.ctaButton.setOnClickListener {
            Toast.makeText(this, R.string.detail_cta_mock, Toast.LENGTH_SHORT).show()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: DetailUiState) {
        val card = state.card
        binding.detailContent.isVisible = card != null
        binding.missingText.isVisible = card == null
        supportActionBar?.title = card?.ad?.title ?: getString(R.string.app_name)

        if (card != null) {
            renderCard(card)
        }
    }

    private fun renderCard(card: FeedCardUiModel) = with(binding) {
        val context = root.context
        coverLabel.text = card.ad.title
        coverLabel.setBackgroundResource(card.ad.channel.coverColorRes())
        titleText.text = card.ad.title
        brandText.text = card.ad.brand
        summaryText.text = card.ad.summary
        val recommendationBasis = card.recommendationReasonBasis()
        recommendationReasonText.text = context.getString(
            R.string.detail_recommendation_reason_format,
            recommendationBasis.channelTitle,
            recommendationBasis.tagSummary,
            recommendationBasis.brand
        )
        tagsText.text = card.tagsLabel()
        descriptionText.text = card.ad.description
        statsText.text = card.statsLabel()
        likeButton.text = card.likeLabel(context)
        collectButton.text = card.collectLabel(context)
    }

    companion object {
        private const val EXTRA_AD_ID = "extra_ad_id"

        fun createIntent(context: Context, adId: String): Intent {
            return Intent(context, DetailActivity::class.java)
                .putExtra(EXTRA_AD_ID, adId)
        }
    }
}

internal data class RecommendationReasonBasis(
    val channelTitle: String,
    val tagSummary: String,
    val brand: String
)

internal fun FeedCardUiModel.recommendationReasonBasis(): RecommendationReasonBasis {
    val tagSummary = ad.tags.take(MAX_RECOMMENDATION_TAGS).joinToString(", ")
        .ifBlank { "content" }
    return RecommendationReasonBasis(
        channelTitle = ad.channel.title,
        tagSummary = tagSummary,
        brand = ad.brand
    )
}

private const val MAX_RECOMMENDATION_TAGS = 2
