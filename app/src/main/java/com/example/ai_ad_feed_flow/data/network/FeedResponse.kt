package com.example.ai_ad_feed_flow.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class FeedResponseDto(
    val page: Int,
    val hasMore: Boolean,
    val items: List<AdItemDto>
)

@JsonClass(generateAdapter = false)
data class AdItemDto(
    val id: String,
    val type: String,
    val title: String,
    val brand: String = "",
    val summary: String = "",
    val tags: List<String> = emptyList(),
    @Json(name = "cover") val coverUrl: String,
    val videoUrl: String? = null,
    val images: List<String> = emptyList(),
    val description: String = ""
)

@JsonClass(generateAdapter = false)
data class SearchRequestDto(
    val query: String
)

@JsonClass(generateAdapter = false)
data class SearchResponseDto(
    val results: List<SearchResultDto>
)

@JsonClass(generateAdapter = false)
data class SearchResultDto(
    val id: String,
    val reason: String
)
