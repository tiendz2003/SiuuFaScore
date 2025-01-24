package com.example.minilivescore.data.repository

import com.example.minilivescore.data.firebase.FCMService
import com.example.minilivescore.data.firebase.FirebaseService
import com.example.minilivescore.data.model.football.FavoriteTeam
import com.example.minilivescore.data.model.football.TeamMatches
import com.example.minilivescore.data.networking.LiveScoreService
import com.example.minilivescore.domain.repository.FavoriteRepository
import com.example.minilivescore.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteTeamRepositoryImpl @Inject constructor(
    firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val firebaseService: FirebaseService,
    private val fcmService: FCMService,
    private val liveScoreService: LiveScoreService
) : FavoriteRepository {
    private val userId = firebaseAuth.currentUser?.uid?:throw Exception("Chưa đăng nhập")

    override fun getFavoriteTeam():Flow<List<FavoriteTeam>> = callbackFlow {
       val listener = fireStore.collection("users")
           .document(userId)
           .collection("favoriteTeams")
           .addSnapshotListener { snapshot, error ->
               if (error != null) {
                   close(error)
                   return@addSnapshotListener
           }
               val teams = snapshot?.documents?.mapNotNull {doc->
                   doc.toObject(FavoriteTeam::class.java)

               }?: emptyList()
               trySend(teams)
           }
         awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)
    override fun getEnableNotificationTeam():Flow<List<FavoriteTeam>> = callbackFlow {
        val listener = fireStore.collection("users")
            .document(userId)
            .collection("favoriteTeams")
            .whereEqualTo("notificationEnabled", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                //ánh xạ dữ liệu từ firestore
                val teams = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(FavoriteTeam::class.java)

                } ?: emptyList()
                trySend(teams)
            }
        awaitClose { listener.remove() }
    }
    override suspend fun addFavoriteTeam(team:FavoriteTeam){
            fireStore.collection("users")
                .document(userId)
                .collection("favoriteTeams")
                .document(team.id)
                .set(team)
                .await()

    }
    override suspend fun removeFavoriteTeam(teamId:String){
        fireStore.collection("users")
            .document(userId!!)
            .collection("favoriteTeams")
            .document(teamId)
            .delete()
            .await()
    }

    override suspend fun updateNotificationPref(teamId: String, isEnable: Boolean){
        firebaseService.updateNotificationPref(userId, teamId, isEnable)
        if(isEnable){
            fcmService.subscribeToTopic(teamId)
        }else{
            fcmService.unsubscribeToTopic(teamId)
        }
    }
    override suspend fun getMatchesByID(teamId: String): Resource<TeamMatches> {
        return withContext(Dispatchers.IO) {
            try {
                val response = liveScoreService.getMatchesTeams(teamId) // Gọi API để lấy danh sách trận đấu
                if (response.isSuccessful) {
                    response.body()?.let { teamMatches ->
                        return@withContext Resource.Success(teamMatches) // Trả về kết quả thành công
                    }
                    return@withContext Resource.Error("Trống không có gì cả em ơi!!!") // Body rỗng
                } else {
                    return@withContext Resource.Error("Error: ${response.code()} - ${response.message()}") // Lỗi từ server
                }
            } catch (e: Exception) {
                return@withContext Resource.Error("Exception occurred: ${e.message}") // Lỗi bất ngờ
            }
        }
    }

}