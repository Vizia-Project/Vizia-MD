package com.capstone.viziaproject.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailHistoryResponse(

	@field:SerializedName("data")
	val data: DataHistoryDetail,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
): Parcelable

@Parcelize
data class DataHistoryDetail(

	@field:SerializedName("date")
	val date: String,

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("infection_status")
	val infectionStatus: String,

	@field:SerializedName("question_result")
	val questionResult: List<Int>,

	@field:SerializedName("prediction_result")
	val predictionResult: String,

	@field:SerializedName("accuracy")
	val accuracy: Double,

	@field:SerializedName("information")
	val information: String,

	@field:SerializedName("id")
	val id: Int
): Parcelable
