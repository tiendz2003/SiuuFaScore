package com.example.minilivescore

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LiveScoreMiniApplication:Application(){
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}