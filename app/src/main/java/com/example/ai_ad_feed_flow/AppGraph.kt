package com.example.ai_ad_feed_flow

import android.content.Context
import com.example.ai_ad_feed_flow.data.network.FeedApi
import com.example.ai_ad_feed_flow.data.repository.FeedRepository
import com.example.ai_ad_feed_flow.data.source.MockFeedDataSource
import com.example.ai_ad_feed_flow.data.source.RetrofitFeedDataSource
import com.example.ai_ad_feed_flow.data.store.InteractionStore
import com.example.ai_ad_feed_flow.player.PlayerPool
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object AppGraph {
    // Toggle to false once the backend is running to switch to real network data.
    private const val USE_MOCK = true

    private val interactionStore = InteractionStore()

    val feedRepository: FeedRepository by lazy {
        FeedRepository(
            dataSource = if (USE_MOCK) MockFeedDataSource() else RetrofitFeedDataSource(feedApi),
            interactionStore = interactionStore
        )
    }

    lateinit var playerPool: PlayerPool
        private set

    fun init(context: Context) {
        playerPool = PlayerPool(context.applicationContext)
    }

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    private val feedApi: FeedApi by lazy {
        retrofit.create(FeedApi::class.java)
    }

    // Replace with your actual backend URL (e.g. "http://10.0.2.2:8000/" for emulator)
    private const val BASE_URL = "http://10.0.2.2:8000/"
}
