package com.example.minilivescore.data.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.minilivescore.worker.MatchNotificationWorker
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface WorkerModule {
    @Binds
    fun bindWorkerFactory(factory: HiltWorkerFactory): WorkerFactory
}
@AssistedFactory
interface MatchNotificationWorkerFactory {
    fun create(
        @Assisted context: Context,
        @Assisted params: WorkerParameters
    ): MatchNotificationWorker
}
// Thêm HiltWorkerFactory
@Module
@InstallIn(SingletonComponent::class)
object WorkerFactoryModule{
    @Provides
    @Singleton
    fun provideWorkerFactory(
        workerFactory :HiltWorkerFactory)
    :Configuration{
        Log.d("MatchNotification", "Providing WorkerFactory")
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
    }
}
@Module
@InstallIn(SingletonComponent::class)
object SharedPreferencesModule {
    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
    }
}



