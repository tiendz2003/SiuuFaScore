package com.example.minilivescore.data.firebase

import com.google.firebase.messaging.FirebaseMessaging
import javax.inject.Inject
//Sử dụng nếu muốn sử dụng nhận thông báo từ FCM
class FCMService @Inject constructor(
    private val messaging: FirebaseMessaging
){
    suspend fun subscribeToTopic(topic: String) = messaging.subscribeToTopic(topic)
    suspend fun unsubscribeToTopic(topic: String) = messaging.unsubscribeFromTopic(topic)
}