package com.capstone.viziaproject.data.repository

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.capstone.viziaproject.data.database.History
import com.capstone.viziaproject.data.database.HistoryDao
import com.capstone.viziaproject.data.database.HistoryRoomDatabase
import com.capstone.viziaproject.data.pref.UserPreference
import com.capstone.viziaproject.data.pref.dataStore
import com.capstone.viziaproject.data.retrofit.ApiConfig
import com.capstone.viziaproject.data.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryRepository private constructor(
    private val apiService: ApiService,
    private val historyDao: HistoryDao
) {

    fun getAllHistories(userId: Int): LiveData<List<History?>> {
        return historyDao.getAllHistory(userId)
    }

    fun getHistoryEventById(id: Int): LiveData<History?> {
        return historyDao.getHistoryById(id)
    }

    suspend fun insert(history: History) {
        withContext(Dispatchers.IO) {
            try {
                historyDao.insert(history)
                Log.d("HistoryRepository", "History successfully inserted: $history")
            } catch (e: Exception) {
                Log.e("HistoryRepository", "Error inserting history: ${e.message}")
            }
        }
    }

    suspend fun delete(history: History) {
        withContext(Dispatchers.IO) {
            try {
                historyDao.delete(history)
                Log.d("HistoryRepository", "History successfully deleted: $history")
            } catch (e: Exception) {
                Log.e("HistoryRepository", "Error deleting history: ${e.message}")
            }
        }
    }

    suspend fun update(history: History) {
        withContext(Dispatchers.IO) {
            try {
                historyDao.update(history)
                Log.d("HistoryRepository", "History successfully updated: $history")
            } catch (e: Exception) {
                Log.e("HistoryRepository", "Error updating history: ${e.message}")
            }
        }
    }

    companion object {
        @Volatile
        private var instance: HistoryRepository? = null

        fun getInstance(context: Context): HistoryRepository {
            return instance ?: synchronized(this) {
                instance ?: createInstance(context).also { instance = it }
            }
        }

        private fun createInstance(context: Context): HistoryRepository {
            val database = HistoryRoomDatabase.getDatabase(context)
            val apiService = ApiConfig.getApiService(UserPreference.getInstance(context.dataStore))
            return HistoryRepository(apiService, database.historyDao())
        }
    }
}


