package com.example.minilivescore.data.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.example.minilivescore.BuildConfig
import com.example.minilivescore.data.firebase.FirebaseService
import com.example.minilivescore.data.networking.LiveScoreService
import com.example.minilivescore.data.networking.YouTubeApiService
import com.example.minilivescore.utils.AuthorizationInterceptor
import com.example.minilivescore.utils.YouTubeAuthorizationInterceptor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @FootBallApi
    fun provideFootBallBaseUrl():String = "https://api.football-data.org/v4/"


    @Provides
    @YouTubeApi
    fun provideYoutubeBaseUrl():String = "https://www.googleapis.com/youtube/v3/"


    @Provides
    @Singleton
    fun provideMoshi():Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    @Provides
    fun provideHttpLoggingInterceptor():HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    }
    @Provides
    @FootBallClientId
    fun provideFootBallClientId() = BuildConfig.API_KEY


    @Provides
    @YoutubeClientId
    fun provideYoutubeClientId() = BuildConfig.YOUTUBE_API_KEY


    @FootBallOkHttpClient
    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authorizationInterceptor: AuthorizationInterceptor
    ):OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addNetworkInterceptor(httpLoggingInterceptor)
        .addInterceptor(authorizationInterceptor)
        .build()


    @YouTubeOkHttpClient
    @Provides
    @Singleton
    fun provideYoutubeOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        youTubeAuthorizationInterceptor: YouTubeAuthorizationInterceptor
    ):OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addNetworkInterceptor(httpLoggingInterceptor)
        .addInterceptor(youTubeAuthorizationInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideAuthorizationInterceptor(
        @FootBallClientId clientId: String
    ): AuthorizationInterceptor = AuthorizationInterceptor(clientId)

    @Provides
    @Singleton
    fun provideYouTubeAuthorizationInterceptor(
        @YoutubeClientId apiKey: String
    ): YouTubeAuthorizationInterceptor = YouTubeAuthorizationInterceptor(apiKey)

    @FootBallApi
    @Provides
    @Singleton
    fun provideFootballRetrofit(
        @FootBallApi baseUrl: String,
        moshi: Moshi,
        @FootBallOkHttpClient okHttpClient: OkHttpClient
    ):Retrofit = provideRetrofit(baseUrl,moshi,okHttpClient)

    @YouTubeApi
    @Provides
    @Singleton
    fun provideYoutubeRetrofit(
        @YouTubeApi baseUrl: String,
        moshi: Moshi,
        @YouTubeOkHttpClient  okHttpClient: OkHttpClient
    ):Retrofit = provideRetrofit(baseUrl,moshi,okHttpClient)

    private fun provideRetrofit(baseUrl: String, moshi: Moshi, okHttpClient: OkHttpClient):Retrofit{
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }
    @Provides
    @Singleton
    fun provideAuth():FirebaseAuth{
        return FirebaseAuth.getInstance()
    }
    @Provides
    @Singleton
    fun provideFireStore():FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }
    @Provides
    @Singleton
    fun provideFcm():FirebaseMessaging{
        return FirebaseMessaging.getInstance()
    }
    @Provides
    @Singleton
    fun provideCredential( @ApplicationContext context:Context):CredentialManager{
        return CredentialManager.create(context)
    }
    @Provides
    @Singleton
    fun provideFootBallApiService(@FootBallApi retrofit:Retrofit): LiveScoreService = retrofit.create(LiveScoreService::class.java)

    @Provides
    @Singleton
    fun provideYoutubeApiService(@YouTubeApi retrofit:Retrofit): YouTubeApiService = retrofit.create(YouTubeApiService::class.java)
}
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FootBallApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FootBallClientId
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class YoutubeClientId

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class YouTubeApi

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FootBallOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class YouTubeOkHttpClient