package com.example.minilivescore

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.minilivescore.worker.MatchNotificationWorker
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class LiveScoreMiniApplication:Application(),Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory


    override fun onCreate() {
        super.onCreate()
        Log.d("MatchNotification", "Application onCreate")
        Log.d("MatchNotification", "WorkManager.getInstance().getWorkInfosByTag() = ${
            WorkManager.getInstance(this).getWorkInfosByTag("match_notifications")
        }")
        FirebaseApp.initializeApp(this)
        val pref = getSharedPreferences("worker_prefs", Context.MODE_PRIVATE)
        val isWorkerScheduled = pref.getBoolean("is_match_notification_scheduled", false)
        if (!isWorkerScheduled) {
            // Kiểm tra permission cho Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                    scheduleWorker()
                }
            } else {
                // Với Android < 13, không cần kiểm tra permission
                scheduleWorker()
            }
        }
        WorkManager.getInstance(this)
            .getWorkInfosByTagLiveData("match_notifications")
            .observeForever { workInfos ->
                workInfos?.forEach { workInfo ->
                    Log.d("MatchNotification", """
                    Work ID: ${workInfo.id}
                    State: ${workInfo.state}
                    Tags: ${workInfo.tags}
                    Run Attempt: ${workInfo.runAttemptCount}
                    Output: ${workInfo.outputData}
                """.trimIndent())
                }
            }
        val oneTimeRequest = OneTimeWorkRequestBuilder<MatchNotificationWorker>()
            .addTag("match_notifications_test")
            .build()

        WorkManager.getInstance(this).enqueue(oneTimeRequest)
    }

    private fun scheduleWorker() {
        Log.d("MatchNotification", "Scheduling worker...")
        MatchNotificationWorker.schedule(this)
        getSharedPreferences("worker_prefs",Context.MODE_PRIVATE)
            .edit()
            .putBoolean("is_match_notification_scheduled",true)
            .apply()
    }
    companion object {
        // Thêm method để schedule worker từ bên ngoài (ví dụ: sau khi user cấp permission)
        fun scheduleMatchNotificationWorker(context: Context) {
            MatchNotificationWorker.schedule(context)
            context.getSharedPreferences("worker_prefs", Context.MODE_PRIVATE)
                .edit()
                .putBoolean("is_match_notification_scheduled", true)
                .apply()
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
}