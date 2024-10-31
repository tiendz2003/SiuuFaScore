package com.example.minilivescore.utils

import com.example.minilivescore.data.networking.YouTubeApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object YouTubeServiceLocator{
    private const val YOUTUBE_BASE_URL = "https://www.googleapis.com/youtube/v3/"


    private val moshi:Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }
    private val youtubeRetrofit :Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(YOUTUBE_BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(youtubeOkHttpClient)
            .build()
    }

    private val youtubeOkHttpClient:OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addNetworkInterceptor(httpLoggingInterceptor)
            .addInterceptor(YouTubeAuthoriztionInterceptor(apiKey = com.example.minilivescore.BuildConfig.YOUTUBE_API_KEY))
            .build()

    }
    private val httpLoggingInterceptor
        get() = HttpLoggingInterceptor().apply {
            level = if(com.example.minilivescore.BuildConfig.DEBUG){
                HttpLoggingInterceptor.Level.BODY
            }else{
                HttpLoggingInterceptor.Level.NONE
            }
        }
     val youtubeApiService :YouTubeApiService by lazy {
        youtubeRetrofit.create(YouTubeApiService::class.java)
    }
}