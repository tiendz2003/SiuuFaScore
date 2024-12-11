package com.example.minilivescore.domain.repository

import android.util.Log
import com.example.minilivescore.data.di.IoDispatcher
import com.example.minilivescore.data.model.football.LeagueMatches
import com.example.minilivescore.data.model.football.LeaguesStanding
import com.example.minilivescore.utils.Resource
import com.example.minilivescore.data.model.football.MatchLive
import com.example.minilivescore.data.networking.LiveScoreService
import com.example.minilivescore.presentation.ui.detailmatch.comment.LiveComment
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

@Singleton
class MatchRepository @Inject constructor(
    private val apiService: LiveScoreService,
    private val database:FirebaseDatabase,
    //Nên khai báo luồng chỉ định ở đây không hardcode chúng với withContext
    @IoDispatcher private val ioDispatcher:CoroutineDispatcher
) {
    suspend fun getLeagueMatches(leagueCode: String, round: Int?):Resource<LeagueMatches>{
        return withContext(ioDispatcher){
            try {
                Resource.Success(apiService.getLeagueMatches(leagueCode,round))
            }catch (e:Exception){
                Resource.Error(e.message?:"Đã xảy ra lỗi")
            }
        }
    }
    suspend fun getStandingLeagues(id:String):Response<LeaguesStanding>{
        return withContext(ioDispatcher){
            apiService.getStandingLeagues(id)
        }
    }
    val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // Thêm KotlinJsonAdapterFactory
        .build()
    suspend fun getPlayBackUrl(id:Int):Resource<MatchLive>{
        return withContext(ioDispatcher){
          try {
              val backendApi = Retrofit.Builder()
                  .baseUrl("http://192.168.0.107:8080/")
                  .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
                  .build()
                  .create(LiveScoreService::class.java)
              val response = backendApi.getPlayBackUrl(id)
              Log.d("API Response", response.toString())
              Resource.Success(response)
          }catch (e:Exception){
              Resource.Error(e.message?:"Đã xảy ra lỗi")
          }
        }
    }
    fun getComments(matchId:String):Flow<List<LiveComment>> = callbackFlow {
        val cmts = mutableListOf<LiveComment>()
        val listener = database.reference
            .child("comments")
            .child(matchId)
            .addChildEventListener(object: CommentListener() {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val cmt = snapshot.getValue(LiveComment::class.java)
                    cmt?.let {
                        cmts.add(it)
                        trySend(cmts.toList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                   close(error.toException())
                }
            })
        awaitClose { database.reference.removeEventListener(listener) }

    }
    fun postComment(matchId: String,cmtLive:LiveComment){
        Log.d("PostComment", "matchId: $matchId, comment: $cmtLive")
        database.reference
            .child("comments")
            .child(matchId)
            .push()
            .setValue(cmtLive)
            .addOnSuccessListener {
                Log.d("PostComment", "Comment posted successfully")
            }
            .addOnFailureListener{
                Log.d("PostComment", "Comment posted failed")

            }
    }
}
abstract class CommentListener:ChildEventListener{
    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        TODO("Not yet implemented")
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        TODO("Not yet implemented")
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        TODO("Not yet implemented")
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        TODO("Not yet implemented")
    }

    override fun onCancelled(error: DatabaseError) {
        TODO("Not yet implemented")
    }
}
