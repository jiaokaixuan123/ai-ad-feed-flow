package com.example.ai_ad_feed_flow

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        AppGraph.init(this)
    }
}
