package com.example.minilivescore.utils

import android.app.Application

class LiveScoreMiniApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        LiveScoreMiniServiceLocator.initWith(this)
    }
}