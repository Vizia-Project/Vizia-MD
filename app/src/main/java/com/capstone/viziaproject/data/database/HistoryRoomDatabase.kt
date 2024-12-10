package com.capstone.viziaproject.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper

@Database(entities = [History::class], version = 1, exportSchema = false)
abstract class HistoryRoomDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: HistoryRoomDatabase? = null

        fun getDatabase(context: Context): HistoryRoomDatabase {
            Log.d("coba123", "Memulai inisialisasi database")
            return INSTANCE ?: synchronized(this) {
                Log.d("coba123", "Membuat instance baru dari database")
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HistoryRoomDatabase::class.java,
                    "favorite_database"
                )
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                Log.d("coba123", "Database berhasil diinisialisasi")
                instance
            }
        }
    }
}