package com.capstone.viziaproject.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.capstone.viziaproject.data.pref.UserModel
import com.capstone.viziaproject.data.pref.UserPreference
import com.capstone.viziaproject.data.response.LoginResponse
import com.capstone.viziaproject.data.response.RegisterResponse
import com.capstone.viziaproject.data.response.SignupResponse
import com.capstone.viziaproject.data.retrofit.ApiService

import com.capstone.viziaproject.helper.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository private constructor(private val userPreference: UserPreference,
                                         private val apiService: ApiService
) {

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun register(name: String, email: String, password: String, passwordConfirmation: String): LiveData<Result<SignupResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name, email, password, passwordConfirmation)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val errorMessage = errorBody ?: "Kesalahan tidak diketahui"
            emit(Result.Error(errorMessage))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Kesalahan tidak terduga"))
        }
    }

    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.e("postLogin", "HTTP Exception: ${e.message}")
            try {
                val errorResponse = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parsedError = gson.fromJson(errorResponse, LoginResponse::class.java)
                emit(Result.Success(parsedError))
            } catch (parseException: Exception) {
                Log.e("postLogin", "Error parsing response: ${parseException.message}")
                emit(Result.Error("Error parsing HTTP exception response"))
            }
        } catch (e: Exception) {
            Log.e("postLogin", "General Exception: ${e.message}")
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService, userPreference: UserPreference
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}