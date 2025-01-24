package com.example.minilivescore.data.model.football

import com.google.firebase.firestore.PropertyName

data class FavoriteTeam(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String,
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String,
    @get:PropertyName("logo")
    @set:PropertyName("logo")
    var logo: String,
    @get:PropertyName("notificationEnabled")
    @set:PropertyName("notificationEnabled")
    var notificationEnabled: Boolean = false
){
   constructor():this("","","",false)
}