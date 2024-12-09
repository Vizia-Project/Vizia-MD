package com.capstone.viziaproject.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.capstone.viziaproject.data.response.GetHistoryResponse
import com.capstone.viziaproject.data.response.NewsResponse
import com.capstone.viziaproject.data.response.StoreHistoryResponse
import com.capstone.viziaproject.data.retrofit.ApiService
import com.capstone.viziaproject.helper.Result
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File

class PredictRepository(private val apiService: ApiService) {

    fun add(
        userId: Int,
        date: String,
        image: File,
        questionResult: List<Int>,
        infectionStatus: String,
        predictionResult: String,
        accuracy: Double,
        information: String
    ): LiveData<Result<StoreHistoryResponse>> = liveData {
        emit(Result.Loading)
//        val file = File(image)
        val requestImageFile = image.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "image", image.name, requestImageFile
        )
        // Prepare other fields as RequestBody
//        val imageRequest = image.toString().toRequestBody("text/plain".toMediaTypeOrNull())
//        val userIdRequest = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
//        val dateRequest = date.toRequestBody("text/plain".toMediaTypeOrNull())
//        val questionResultRequest = questionResult.toString().toRequestBody("application/json".toMediaTypeOrNull())
//        val infectionStatusRequest = infectionStatus.toRequestBody("text/plain".toMediaTypeOrNull())
//        val predictionResultRequest = predictionResult.toRequestBody("text/plain".toMediaTypeOrNull())
//        val accuracyRequest = accuracy.toString().toRequestBody("text/plain".toMediaTypeOrNull())
//        val informationRequest = information.toRequestBody("text/html".toMediaTypeOrNull())

        try {
            val response = apiService.storeHistory(
                userId,
                date,
                multipartBody,
//                multipartBody.toString(),
                questionResult,
                infectionStatus,
                predictionResult,
                accuracy,
                information
            )
            Log.d("cekcekAPIResponse", "Response: ${response}")
            emit(Result.Success(response))
        } catch (e: HttpException) {
            try {
                val errorResponse = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parsedError = gson.fromJson(errorResponse, StoreHistoryResponse::class.java)
                emit(Result.Success(parsedError))
            } catch (e: Exception) {
                Log.e("cekcekAPIResponse", "Error parsing error response: ${e.message}")
                emit(Result.Error("Error: ${e.message}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unexpected error"))
        }
    }

    fun adds(
        image: String,
        userId: Int,
        date: String,
        questionResult: List<Int>,
        infectionStatus: String,
        predictionResult: String,
        accuracy: Double,
        information: String
    ): LiveData<Result<StoreHistoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.storeHistoryy(
                image,
                userId,
                date,
                questionResult,
                infectionStatus,
                predictionResult,
                accuracy,
                information
            )
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody ?: "Kesalahan tidak diketahui"
            emit(Result.Error(errorMessage))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Kesalahan tidak terduga"))
        }
    }

    suspend fun getDataHistory(userId: Int): LiveData<Result<GetHistoryResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStories(userId)
            Log.d("cekcekhistory", "Stories Response: $response")
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.e("cekcekhistory", "HTTP Exception: ${e.message}")
            try {
                val errorResponse = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parsedError = gson.fromJson(errorResponse, NewsResponse::class.java)
                emit(Result.Error(parsedError.message ?: "Unknown error"))
            } catch (e: Exception) {
                Log.e("cekcekhistory", "Error parsing error response: ${e.message}")
                emit(Result.Error("Error: ${e.message}"))
            }
        } catch (e: Exception) {
            Log.e("cekcekhistory", "General Exception: ${e.message}")
            emit(Result.Error(e.message.toString()))
        }
    }


    companion object {
        @Volatile
        private var instance: PredictRepository? = null

        fun getInstance(apiService: ApiService): PredictRepository {
            return instance ?: synchronized(this) {
                instance ?: PredictRepository(apiService).also { instance = it }
            }
        }
    }
}
