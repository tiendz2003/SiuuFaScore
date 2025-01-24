package com.example.minilivescore.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.minilivescore.MainActivity
import com.example.minilivescore.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

//Lớp này để dùng nếu muốn nhận thông báo bằng Firebase
class FCMService: FirebaseMessagingService() {
    companion object {
        private const val CHANNEL_ID = "match_notifications"
    }
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        createNotificationChannel(notificationManager)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.images)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel  =NotificationChannel(
            CHANNEL_ID,
            "Thông báo trận đấu",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Trận đấu sắp diễn ra"
            enableLights(true)
            lightColor = R.color.color_primary_default
            enableVibration(true)

        }
        notificationManager.createNotificationChannel(channel)
    }
}