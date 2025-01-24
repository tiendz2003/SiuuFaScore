package com.example.minilivescore.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.minilivescore.MainActivity
import com.example.minilivescore.R
import com.example.minilivescore.data.model.football.TeamMatches
import com.example.minilivescore.data.repository.FavoriteTeamRepositoryImpl
import com.example.minilivescore.domain.repository.FavoriteRepository
import com.example.minilivescore.utils.Preferences
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@HiltWorker
class MatchNotificationWorker @AssistedInject constructor(
    @Assisted context:Context,
    @Assisted params:WorkerParameters,
    private val favRepository: FavoriteRepository,
    private val sharedPreferences: SharedPreferences
):CoroutineWorker(context,params) {
    init {
        Log.d("MatchNotification", "Worker initialized")
    }
    interface Factory {
        fun create(context: Context, params: WorkerParameters): MatchNotificationWorker
    }
    override suspend fun doWork(): Result {
        Log.d("MatchNotification", "Worker started with runAttemptCount: $runAttemptCount")

        return try {
            Log.d("MatchNotification", "Starting to fetch favorite teams")
            val favEnableTeam =favRepository.getEnableNotificationTeam()
                .first()
                .map {team->
                    team.id
                }
                .toSet()
            Log.d("MatchNotification", "Tìm thấy ${favEnableTeam.size} đội bóng")
            if(favEnableTeam.isEmpty()){
                Log.d("MatchNotification", "Không có")
                return Result.success()
            }
                //cách này cũng được nhưng tốn kém quá
          /*  val listUpComingMatch = coroutineScope {
                favEnableTeam.map {teamId ->
                    async {
                         favRepository.getMatchesByID(teamId)
                    }
                }.awaitAll()
            }*/
            //Delay 6s mỗi lần lấy dữ liệu trận cho 1 đội bóng
            val listUpComingMatch = mutableListOf<TeamMatches>()
            for (teamId in favEnableTeam) {
                try {
                    val matches = favRepository.getMatchesByID(teamId)
                    listUpComingMatch.add(matches.data!!)
                    delay(6000)
                }catch (e:Exception){
                    Log.e("MatchNotification", "Lỗi tìm kiếm trận cho đội bóng  $teamId", e)
                    // Tiếp tục nếu việc dữ liệu của 1 đội bị lỗi
                    continue
                }
            }
                val upComingMatch = listUpComingMatch.flatMap {
                it.matches ?: emptyList()
            }.filter {match->
                    val matchTime = ZonedDateTime.parse(match.utcDate)
                    val now = ZonedDateTime.now(ZoneId.of("UTC"))
                    match.homeTeam.id.toString() in favEnableTeam || match.awayTeam.id.toString() in favEnableTeam
                        && match.status == "SCHEDULED" && matchTime.isAfter(now)
            //Lọc đội bóng yêu thích và các trận chưa đá
            }
            upComingMatch.forEach { match ->

                val timeUntilMatch = Preferences.difTime(match.utcDate)
                val matchId = match.id.toString()
                val notification = sharedPreferences.getBoolean("notification_sent_$matchId",false)
                Log.d("MatchNotification", """
                Trận : ${match.homeTeam.name} vs ${match.awayTeam.name}
                UTC Date: ${match.utcDate}
                Thời gian đến trận đấu: ${timeUntilMatch.toHours()}
                Thông báo: $notification
            """.trimIndent())
                //Chỉ gửi thông báo đến các trận đấu sắp diễn ra trong 24h tới và đánh dấu thông báo
                if(timeUntilMatch.toHours() in 0..24 && !notification){
                    Log.d("MatchNotification", "Đang tạo thông báo cho trận có ID: $matchId")
                    createMatchNotification(match)
                    sharedPreferences.edit().putBoolean("notification_sent_$matchId", true).apply()
                }
            }
            cleanUpOldNotifications(upComingMatch.map {it.id.toString()}.toSet())
            Result.success()
        }catch (e:Exception){
            Result.failure()
        }
    }

    private fun cleanUpOldNotifications(currentMatchId: Set<String>) {
        //Xóa flag thông báo cũ
        val editor = sharedPreferences.edit()
        sharedPreferences.all.keys
            .filter {
                it.startsWith("notification_sent_")
            }
            .forEach { key->
                val matchId = key.removePrefix("notification_sent_")
                if(matchId !in currentMatchId){
                    editor.remove(key)
                }
            }
        editor.apply()
    }

    private fun createMatchNotification(match: TeamMatches.Matche) {
        val notificationManager = applicationContext.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        // Tạo notification channel (required for Android O and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                MATCH_CHANNEL_ID,
                "Match Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Trận đấu sắp diễn ra"
                enableLights(true)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent để mở app khi click vào notification
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("matchId", match.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val matchTime = Preferences.formatDate(match.utcDate)
        val notification = NotificationCompat.Builder(applicationContext, MATCH_CHANNEL_ID)
            .setContentTitle("Trận đấu sắp diễn ra!")
            .setContentText("${match.homeTeam.name} vs ${match.awayTeam.name}")
            .setSubText(matchTime)
            .setSmallIcon(R.drawable.images)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .build()

        notificationManager.notify(match.id.hashCode(), notification)

    }
    companion object {
        private const val MATCH_CHANNEL_ID = "match_notifications"

        // Hàm để lập lịch worker
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)//Chỉ khi có internet
                .build()

            // Chạy worker 15 phút một lần
            val request = PeriodicWorkRequestBuilder<MatchNotificationWorker>(
                5, TimeUnit.HOURS ,// initial delay,
                5, TimeUnit.MINUTES // flex period
            )
                .setConstraints(constraints)
                .addTag("match_notifications")
                .setInitialDelay(1, TimeUnit.MINUTES)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            WorkManager.getInstance(context).also {workManager->
                Log.d("MatchNotification", "Đang lập lịch work mới")
                    workManager.enqueueUniquePeriodicWork(
                        "match_notifications",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        request
                    ).result.addListener(
                        {
                            Log.d("MatchNotification", "Work enqueued successfully")
                        },
                        ContextCompat.getMainExecutor(context)
                    )
                workManager.getWorkInfoByIdLiveData(request.id).observeForever { workInfo ->
                    Log.d("MatchNotification", "Trạng thái của luồng work: ${workInfo?.state}")
                }
            }

            // Thêm log khi schedule
            Log.d("MatchNotification", "Lập lịch worker thành công")
        }
    }
}