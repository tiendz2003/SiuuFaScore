package com.example.minilivescore.data.di

import com.example.minilivescore.data.database.LiveScoreDao
import com.example.minilivescore.data.firebase.FCMService
import com.example.minilivescore.data.firebase.FirebaseService
import com.example.minilivescore.data.networking.LiveScoreService
import com.example.minilivescore.data.networking.YouTubeApiService
import com.example.minilivescore.domain.repository.FavoriteRepository
import com.example.minilivescore.data.repository.FavoriteTeamRepositoryImpl
import com.example.minilivescore.domain.repository.HighlightRepository
import com.example.minilivescore.data.repository.HighlightRepositoryImpl
import com.example.minilivescore.domain.repository.MatchRepository
import com.example.minilivescore.data.repository.MatchRepositoryImpl
import com.example.minilivescore.data.repository.SearchRepositoryImpl
import com.example.minilivescore.domain.repository.SearchRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideMatchRepository(
        apiService: LiveScoreService,
        database:FirebaseDatabase,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): MatchRepository {
        return MatchRepositoryImpl(apiService, database,ioDispatcher)
    }
    @Provides
    @Singleton
    fun provideHighlightRepository(
        youTubeApiService: YouTubeApiService
    ):HighlightRepository{
        return HighlightRepositoryImpl(youTubeApiService)
    }

    @Provides
    @Singleton
    fun provideFavoriteTeamRepository(
        firebaseAuth: FirebaseAuth,
        fireStore: FirebaseFirestore,
        firebaseService: FirebaseService,
        fcmService: FCMService,
        liveScoreService: LiveScoreService
    ): FavoriteRepository {
        return FavoriteTeamRepositoryImpl(firebaseAuth, fireStore, firebaseService, fcmService, liveScoreService)
    }
    @Provides
    @Singleton
    fun provideSearchRepository(
        liveScoreApiService: LiveScoreService,
        livescoreDao: LiveScoreDao
    ):SearchRepository{
        return SearchRepositoryImpl(liveScoreApiService,livescoreDao)
    }
}