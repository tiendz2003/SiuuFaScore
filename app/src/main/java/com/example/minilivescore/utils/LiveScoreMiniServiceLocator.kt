package com.example.minilivescore.utils

import androidx.annotation.MainThread
import com.airbnb.lottie.BuildConfig
import com.example.minilivescore.BuildConfig.API_KEY
import com.example.minilivescore.LiveScoreMiniApplication
import com.example.minilivescore.data.networking.LiveScoreService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object LiveScoreMiniServiceLocator {
    private const val BASE_URL = "https://api.football-data.org/v4/"

    private var _application: LiveScoreMiniApplication?= null
    @MainThread
    fun initWith(app: LiveScoreMiniApplication) {
        _application = app
    }
    @get:MainThread
    val application: LiveScoreMiniApplication
        get() = checkNotNull(_application){
            "LiveScoreMiniServiceLocator must be initialized. " +
                    "Call LiveScoreMiniServiceLocator.initWith(this) in your Application class."
        }
    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private val authorizationInterceptor: AuthorizationInterceptor
        get() = AuthorizationInterceptor(clientId = API_KEY)

    private val retrofit :Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }
    private val httpLoggingInterceptor
        get() = HttpLoggingInterceptor().apply {
            level = if(BuildConfig.DEBUG){
                HttpLoggingInterceptor.Level.BODY
            }else{
                HttpLoggingInterceptor.Level.NONE
            }
        }
     val okHttpClient:OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .addNetworkInterceptor(httpLoggingInterceptor)
            .addInterceptor(authorizationInterceptor)
            .build()
    }
     val liveScoreApiService : LiveScoreService by lazy { LiveScoreService(retrofit = retrofit) }
}