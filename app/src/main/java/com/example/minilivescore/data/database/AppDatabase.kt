package com.example.minilivescore.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.minilivescore.data.model.football.CoachEntity
import com.example.minilivescore.data.model.football.PlayerEntity
import com.example.minilivescore.data.model.football.TeamEntity


@Database(entities = [TeamEntity::class, PlayerEntity::class, CoachEntity::class], version = 1)
abstract class AppDatabase():RoomDatabase() {
    abstract fun LiveScoreDao():LiveScoreDao
    companion object{
        @Volatile
        private var instance:AppDatabase?=null

        fun getDatabase(context:Context):AppDatabase{
            return instance?: synchronized(this){
                val db = Room.databaseBuilder(context.applicationContext,
                    AppDatabase::class.java,"teams.db")
                    .build()
                instance = db
                db
            }
        }
    }
}