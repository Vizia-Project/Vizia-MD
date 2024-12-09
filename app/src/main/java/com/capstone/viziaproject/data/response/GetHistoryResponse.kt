package com.capstone.viziaproject.data.response

import com.google.gson.annotations.SerializedName

data class GetHistoryResponse(

	@field:SerializedName("data")
	val data: List<DataItemHistory>,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class DataItemHistory(

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("prediction_result")
	val predictionResult: String,

	@field:SerializedName("accuracy")
	val accuracy: Any,

	@field:SerializedName("id")
	val id: Int
)
