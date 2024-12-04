package com.capstone.viziaproject.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.capstone.viziaproject.data.pref.UserPreference
import com.capstone.viziaproject.data.response.DetailNewsResponse
import com.capstone.viziaproject.data.response.NewsResponse
import com.capstone.viziaproject.data.retrofit.ApiService
import com.capstone.viziaproject.helper.Result
import com.google.gson.Gson
import retrofit2.HttpException

class NewsRepository(private val apiService: ApiService) {

    suspend fun getDetailStory(storyUrl: String): DetailNewsResponse{
        return apiService.getDetailStory(storyUrl)
    }

    fun getNews(): LiveData<Result<NewsResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getNews()
            Log.d("cekcek123", "Stories Response: $response")
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.e("cekcek123", "HTTP Exception: ${e.message}")
            try {
                val errorResponse = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parsedError = gson.fromJson(errorResponse, NewsResponse::class.java)
                emit(Result.Error(parsedError.message ?: "Unknown error"))
            } catch (e: Exception) {
                Log.e("cekcek123", "Error parsing error response: ${e.message}")
                emit(Result.Error("Error: ${e.message}"))
            }
        } catch (e: Exception) {
            Log.e("cekcek123", "General Exception: ${e.message}")
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: NewsRepository? = null

        fun getInstance(apiService: ApiService, pref: UserPreference): NewsRepository {
            return instance ?: synchronized(this) {
                instance ?: NewsRepository(apiService).also { instance = it }
            }
        }
    }
}