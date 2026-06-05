package com.example.ai_ad_feed_flow.player

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import okhttp3.OkHttpClient
import java.io.File

class PlayerPool(private val context: Context, private val poolSize: Int = 2) {

    private val players = ArrayDeque<ExoPlayer>()
    private val activeMap = mutableMapOf<Any, ExoPlayer>()

    val videoCache: SimpleCache by lazy {
        SimpleCache(
            File(context.cacheDir, "video_cache"),
            LeastRecentlyUsedCacheEvictor(VIDEO_CACHE_SIZE)
        )
    }

    private val cacheDataSourceFactory by lazy {
        val upstreamFactory = OkHttpDataSource.Factory(OkHttpClient())
        CacheDataSource.Factory()
            .setCache(videoCache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    init {
        repeat(poolSize) { players.addLast(createPlayer()) }
    }

    /**
     * Bind a player to a holder key (e.g. ViewHolder instance).
     * Returns the player instance; caller owns the PlayerView binding.
     */
    fun bindPlayer(holderKey: Any, videoUrl: String): ExoPlayer {
        activeMap[holderKey]?.let { return it }

        val player = players.removeFirstOrNull() ?: createPlayer()
        activeMap[holderKey] = player

        val mediaItem = androidx.media3.common.MediaItem.fromUri(videoUrl)
        player.setMediaItem(mediaItem)
        player.prepare()
        return player
    }

    /**
     * Unbind a player from its holder, pause and return it to the pool.
     */
    fun unbindPlayer(holderKey: Any) {
        val player = activeMap.remove(holderKey) ?: return
        player.pause()
        player.clearMediaItems()
        players.addLast(player)
    }

    /**
     * Release all players and the video cache. Call from Application or owning scope onDestroy.
     */
    fun release() {
        activeMap.values.forEach { it.release() }
        activeMap.clear()
        players.forEach { it.release() }
        players.clear()
        videoCache.release()
    }

    private fun createPlayer(): ExoPlayer {
        return ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_OFF
                volume = 0f
            }
    }

    companion object {
        private const val VIDEO_CACHE_SIZE = 100L * 1024 * 1024 // 100 MB
    }
}
