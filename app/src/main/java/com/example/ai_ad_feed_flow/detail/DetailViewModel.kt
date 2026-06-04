package com.example.ai_ad_feed_flow.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ai_ad_feed_flow.data.repository.FeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailViewModel(
    private val adId: String,
    private val repository: FeedRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        repository.recordClick(adId)
        refreshCard()
        viewModelScope.launch {
            repository.interactionStates.collect {
                refreshCard()
            }
        }
    }

    fun toggleLike() {
        repository.toggleLike(adId)
    }

    fun toggleCollect() {
        repository.toggleCollect(adId)
    }

    fun share() {
        repository.share(adId)
    }

    private fun refreshCard() {
        val card = repository.getCard(adId)
        _uiState.update {
            if (card == null) {
                DetailUiState(errorMessage = "missing")
            } else {
                DetailUiState(card = card)
            }
        }
    }

    class Factory(
        private val adId: String,
        private val repository: FeedRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
                return DetailViewModel(adId, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
