package com.example.minilivescore.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.minilivescore.data.model.TeamEntity


@Database(entities = [TeamEntity::class], version = 1)
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