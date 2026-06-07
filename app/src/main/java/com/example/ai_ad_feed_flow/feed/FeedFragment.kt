package com.example.ai_ad_feed_flow.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ai_ad_feed_flow.AppGraph
import com.example.ai_ad_feed_flow.R
import com.example.ai_ad_feed_flow.data.model.FeedCardUiModel
import com.example.ai_ad_feed_flow.data.model.FeedChannel
import com.example.ai_ad_feed_flow.databinding.FragmentFeedBinding
import com.example.ai_ad_feed_flow.detail.DetailActivity
import com.example.ai_ad_feed_flow.feed.adapter.FeedAdapter
import kotlinx.coroutines.launch

class FeedFragment : Fragment() {
    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!

    private val channel: FeedChannel by lazy {
        FeedChannel.fromName(arguments?.getString(ARG_CHANNEL))
    }

    private val viewModel: FeedViewModel by viewModels {
        FeedViewModel.Factory(channel, AppGraph.feedRepository)
    }

    private val feedAdapter = FeedAdapter(
        onCardClick = ::openDetail,
        onLikeClick = { viewModel.toggleLike(it.ad.id) },
        onCollectClick = { viewModel.toggleCollect(it.ad.id) },
        onShareClick = { viewModel.share(it.ad.id) }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupRefresh()
        collectState()

        if (viewModel.uiState.value.items.isEmpty()) {
            viewModel.loadFirstPage()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.feedRecycler.adapter = null
        _binding = null
    }

    private fun setupRecyclerView() = with(binding.feedRecycler) {
        adapter = feedAdapter
        layoutManager = LinearLayoutManager(requireContext())
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy <= 0) return
                if (feedAdapter.itemCount == 0) return
                val manager = recyclerView.layoutManager as? LinearLayoutManager ?: return
                val lastVisible = manager.findLastVisibleItemPosition()
                val totalItems = feedAdapter.itemCount
                if (lastVisible >= totalItems - LOAD_MORE_THRESHOLD) {
                    viewModel.loadNextPage()
                }
            }
        })
    }

    private fun setupRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
        binding.retryButton.setOnClickListener {
            viewModel.refresh()
        }
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::render)
            }
        }
    }

    private fun render(state: FeedUiState) {
        val statusVisibility = state.toFeedStatusVisibility()
        feedAdapter.submitList(state.items)
        binding.swipeRefresh.isRefreshing = state.isRefreshing
        binding.emptyText.isVisible = statusVisibility.showEmpty
        binding.loadingMoreText.isVisible = statusVisibility.showLoadingMore
        binding.errorGroup.isVisible = statusVisibility.showError
        binding.errorText.text = state.errorMessage ?: getString(R.string.feed_error_default)
    }

    private fun openDetail(card: FeedCardUiModel) {
        startActivity(DetailActivity.createIntent(requireContext(), card.ad.id))
    }

    companion object {
        private const val ARG_CHANNEL = "channel"
        private const val LOAD_MORE_THRESHOLD = 2

        fun newInstance(channel: FeedChannel): FeedFragment {
            return FeedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CHANNEL, channel.name)
                }
            }
        }
    }
}

internal data class FeedStatusVisibility(
    val showEmpty: Boolean,
    val showLoadingMore: Boolean,
    val showError: Boolean
)

internal fun FeedUiState.toFeedStatusVisibility(): FeedStatusVisibility {
    val hasError = errorMessage != null
    return FeedStatusVisibility(
        showEmpty = items.isEmpty() && !isRefreshing && !hasError,
        showLoadingMore = isLoadingMore,
        showError = hasError
    )
}
