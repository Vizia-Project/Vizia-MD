package com.capstone.viziaproject.data.response

data class UserResponse(
	val data: User? = null,
	val message: String? = null,
	val status: String? = null
)

data class User(
	val name: String? = null,
	val photo: String? = null,
	val email: String? = null
)

