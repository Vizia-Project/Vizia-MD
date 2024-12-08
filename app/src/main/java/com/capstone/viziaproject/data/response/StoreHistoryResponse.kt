package com.capstone.viziaproject.data.response

import com.google.gson.annotations.SerializedName

data class StoreHistoryResponse(

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)
