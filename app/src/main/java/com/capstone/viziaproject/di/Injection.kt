package com.capstone.viziaproject.di

import android.content.Context
import com.capstone.viziaproject.data.pref.UserPreference
import com.capstone.viziaproject.data.pref.dataStore
import com.capstone.viziaproject.data.repository.HistoryRepository
import com.capstone.viziaproject.data.repository.NewsRepository
import com.capstone.viziaproject.data.repository.PredictRepository
import com.capstone.viziaproject.data.repository.UserRepository
import com.capstone.viziaproject.data.retrofit.ApiConfig

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return UserRepository.getInstance(apiService, pref)
    }
    fun provideNewsRepository(context: Context): NewsRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return NewsRepository.getInstance(apiService, pref)
    }
    fun providePredictRepository(context: Context): PredictRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService(pref)
        return PredictRepository.getInstance(apiService)
    }
//    fun provideHistoryRepository(context: Context): HistoryRepository {
//        val pref = UserPreference.getInstance(context.dataStore)
//        val apiService = ApiConfig.getApiService(pref)
//        return HistoryRepository.getInstance(apiService)
//    }
    fun provideHistoryRepository(context: Context): HistoryRepository {
        return HistoryRepository.getInstance(context)
    }

}