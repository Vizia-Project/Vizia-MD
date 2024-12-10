package com.capstone.viziaproject.data.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.capstone.viziaproject.data.database.History
import com.capstone.viziaproject.data.database.HistoryDao
import com.capstone.viziaproject.data.database.HistoryRoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryRepository(application: Application) {
    private val mHistoryDao: HistoryDao

    init {
        val db = HistoryRoomDatabase.getDatabase(application)
        mHistoryDao = db.historyDao()
    }

    fun getAllHistories(userId: Int): LiveData<List<History?>>{
        return mHistoryDao.getAllHistory(userId)
    }

    fun getHistoryEventById(id: Int): LiveData<History?> {
        return mHistoryDao.getHistoryById(id)
    }

    suspend fun insert(histories: History) {
        withContext(Dispatchers.IO) {
            try {
                mHistoryDao.insert(histories)
                Log.d("FavoriteRepository", "Event successfully inserted to database: $histories")
            } catch (e: Exception) {
                Log.e("FavoriteRepository", "Error inserting event to database: ${e.message}")
            }
        }
    }

    suspend fun delete(histories: History) {
        withContext(Dispatchers.IO) {
            mHistoryDao.delete(histories)
        }
    }

    suspend fun update(histories: History) {
        withContext(Dispatchers.IO) {
            mHistoryDao.update(histories)
        }
    }
}