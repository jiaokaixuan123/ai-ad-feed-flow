package com.example.ai_ad_feed_flow.data.store

import com.example.ai_ad_feed_flow.data.model.InteractionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InteractionStore {
    private val _states = MutableStateFlow<Map<String, InteractionState>>(emptyMap())
    val states: StateFlow<Map<String, InteractionState>> = _states.asStateFlow()

    fun currentState(adId: String): InteractionState {
        return _states.value[adId] ?: InteractionState()
    }

    fun toggleLike(adId: String) {
        update(adId) { it.copy(liked = !it.liked) }
    }

    fun toggleCollect(adId: String) {
        update(adId) { it.copy(collected = !it.collected) }
    }

    fun share(adId: String) {
        update(adId) { it.copy(shareCount = it.shareCount + 1) }
    }

    fun recordClick(adId: String) {
        update(adId) { it.copy(clickCount = it.clickCount + 1) }
    }

    private fun update(adId: String, reducer: (InteractionState) -> InteractionState) {
        _states.update { current ->
            current + (adId to reducer(current[adId] ?: InteractionState()))
        }
    }
}
