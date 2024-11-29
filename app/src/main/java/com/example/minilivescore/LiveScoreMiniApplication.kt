package com.example.minilivescore

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LiveScoreMiniApplication:Application(){
    override fun onCreate() {
        super.onCreate()
    }
}