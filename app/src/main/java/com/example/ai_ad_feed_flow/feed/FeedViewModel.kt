package com.example.ai_ad_feed_flow.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ai_ad_feed_flow.data.model.FeedChannel
import com.example.ai_ad_feed_flow.data.repository.FeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedViewModel(
    private val channel: FeedChannel,
    private val repository: FeedRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private var currentPage = 0

    init {
        viewModelScope.launch {
            repository.interactionStates.collect {
                syncInteractionState()
            }
        }
    }

    fun loadFirstPage() {
        currentPage = FIRST_PAGE
        _uiState.update { it.copy(isRefreshing = true, errorMessage = null) }
        val result = repository.loadPage(
            channel = channel,
            page = FIRST_PAGE,
            pageSize = PAGE_SIZE,
            refresh = true
        )
        _uiState.update {
            it.copy(
                items = result.items,
                isRefreshing = false,
                isLoadingMore = false,
                hasMore = result.hasMore,
                errorMessage = null
            )
        }
    }

    fun refresh() {
        loadFirstPage()
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoadingMore || !state.hasMore) return

        _uiState.update { it.copy(isLoadingMore = true, errorMessage = null) }
        val nextPage = currentPage + 1
        val result = repository.loadPage(
            channel = channel,
            page = nextPage,
            pageSize = PAGE_SIZE
        )
        currentPage = nextPage
        _uiState.update {
            it.copy(
                items = result.items,
                isLoadingMore = false,
                hasMore = result.hasMore,
                errorMessage = null
            )
        }
    }

    fun toggleLike(id: String) {
        repository.toggleLike(id)
    }

    fun toggleCollect(id: String) {
        repository.toggleCollect(id)
    }

    fun share(id: String) {
        repository.share(id)
    }

    fun recordClick(id: String) {
        repository.recordClick(id)
    }

    private fun syncInteractionState() {
        val cards = repository.loadedCards(channel)
        if (cards.isNotEmpty()) {
            _uiState.update { it.copy(items = cards) }
        }
    }

    class Factory(
        private val channel: FeedChannel,
        private val repository: FeedRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
                return FeedViewModel(channel, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    private companion object {
        const val FIRST_PAGE = 1
        const val PAGE_SIZE = 6
    }
}
