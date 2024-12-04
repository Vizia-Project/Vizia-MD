package com.capstone.viziaproject.data.response

import com.google.gson.annotations.SerializedName

data class DetailNewsResponse(

	@field:SerializedName("data")
	val data: DataNews,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class DataNews(

	@field:SerializedName("image")
	val image: String,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("content")
	val content: String
)
