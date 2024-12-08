package com.capstone.viziaproject.data.retrofit

import com.capstone.viziaproject.data.response.DetailNewsResponse
import com.capstone.viziaproject.data.response.LoginResponse
import com.capstone.viziaproject.data.response.NewsResponse
import com.capstone.viziaproject.data.response.RegisterResponse
import com.capstone.viziaproject.data.response.SignupResponse
import com.capstone.viziaproject.data.response.StoreHistoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String
    ): SignupResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("articles")
    suspend fun getNews(): NewsResponse

    @GET("articles/detail")
    suspend fun getDetailStory(
        @Query("url") url: String
    ): DetailNewsResponse

//    @FormUrlEncoded
//    @POST("histories")
//    suspend fun storeHistory(
//        @Field("userId") userId: Int,
//        @Field("date") date: String,
//        @Field("image") image: String,
//        @Field("question_result") questionResult: List<Int>,
//        @Field("infection_status") infectionStatus: String,
//        @Field("prediction_result") predictionResult: String,
//        @Field("accuracy") accuracy: Double,
//        @Field("information") information: String
//    ): StoreHistoryResponse

    @Multipart
    @POST("histories")
    suspend fun storeHistory(
        @Part image: MultipartBody.Part,
        @Part("userId") userId: RequestBody,
        @Part("date") date: RequestBody,
        @Part("question_result") questionResult: RequestBody,
        @Part("infection_status") infectionStatus: RequestBody,
        @Part("prediction_result") predictionResult: RequestBody,
        @Part("accuracy") accuracy: RequestBody,
        @Part("information") information: RequestBody
    ): StoreHistoryResponse


}