package com.capstone.viziaproject.data.retrofit

import com.capstone.viziaproject.data.response.DetailNewsResponse
import com.capstone.viziaproject.data.response.GetHistoryResponse
import com.capstone.viziaproject.data.response.LoginResponse
import com.capstone.viziaproject.data.response.NewsResponse
import com.capstone.viziaproject.data.response.SignupResponse
import com.capstone.viziaproject.data.response.StoreHistoryResponse
import okhttp3.MultipartBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
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
        @Part("user_id") userId: Int,
        @Part("date") date: String,
        @Part("image") image: String,
//        @Part image: MultipartBody.Part,
        @Part("question_result") questionResult: List<Int>,
        @Part("infection_status") infectionStatus: String,
        @Part("prediction_result") predictionResult: String,
        @Part("accuracy") accuracy: Double,
        @Part("information") information: String
    ): StoreHistoryResponse

    @FormUrlEncoded
    @POST("histories")
    suspend fun storeHistoryy(
        @Field("image") image: String,
        @Field("userId") userId: Int,
        @Field("date") date: String,
        @Field("question_result") questionResult: List<Int>,
        @Field("infection_status") infectionStatus: String,
        @Field("prediction_result") predictionResult: String,
        @Field("accuracy") accuracy: Double,
        @Field("information") information: String
    ): StoreHistoryResponse

//    @GET("/histories?user_id={id}")
//    suspend fun getStories(
//        @Path("id") id: Int,
//    ): GetHistoryResponse

    @GET("/histories")
    suspend fun getStories(
        @Query("user_id") userId: Int,
    ): GetHistoryResponse


}