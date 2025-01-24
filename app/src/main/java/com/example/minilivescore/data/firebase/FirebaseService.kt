package com.example.minilivescore.data.firebase

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirebaseService @Inject constructor(
    private val fireStore:FirebaseFirestore
) {
    suspend fun updateNotificationPref(
        userId:String,
        teamId:String,
        isEnable:Boolean,
    ){
        fireStore.collection("users")
            .document(userId)
            .collection("favoriteTeams")
            .document(teamId)
            .update("notificationEnabled",isEnable)

    }
}