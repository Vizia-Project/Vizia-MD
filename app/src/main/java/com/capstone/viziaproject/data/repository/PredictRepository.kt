package com.capstone.viziaproject.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.capstone.viziaproject.data.response.StoreHistoryResponse
import com.capstone.viziaproject.data.retrofit.ApiService
import com.capstone.viziaproject.helper.Result
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class PredictRepository(private val apiService: ApiService) {

    fun add(
        image: File,
        userId: Int,
        date: String,
        questionResult: List<Int>,
        infectionStatus: String,
        predictionResult: String,
        accuracy: Double,
        information: String
    ): LiveData<Result<StoreHistoryResponse>> = liveData {
        emit(Result.Loading)
        val requestImageFile = image.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo", image.name, requestImageFile
        )
        // Prepare other fields as RequestBody
        val userIdRequest = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val dateRequest = date.toRequestBody("text/plain".toMediaTypeOrNull())
        val questionResultRequest = questionResult.joinToString(",").toRequestBody("text/plain".toMediaTypeOrNull())
        val infectionStatusRequest = infectionStatus.toRequestBody("text/plain".toMediaTypeOrNull())
        val predictionResultRequest = predictionResult.toRequestBody("text/plain".toMediaTypeOrNull())
        val accuracyRequest = accuracy.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val informationRequest = information.toRequestBody("text/html".toMediaTypeOrNull())
        try {
            val response = apiService.storeHistory(
                multipartBody,
                userIdRequest,
                dateRequest,
                questionResultRequest,
                infectionStatusRequest,
                predictionResultRequest,
                accuracyRequest,
                informationRequest
            )
            emit(Result.Success(response))
        } catch (e: HttpException) {
            try {
                val errorResponse = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parsedError = gson.fromJson(errorResponse, StoreHistoryResponse::class.java)
                emit(Result.Success(parsedError))
            } catch (e: Exception) {
                Log.e("cekcek123add", "Error parsing error response: ${e.message}")
                emit(Result.Error("Error: ${e.message}"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unexpected error"))
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
