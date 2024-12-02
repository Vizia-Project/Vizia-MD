package com.capstone.viziaproject.helper

sealed class Result<out T> private constructor(){
    object Loading : Result<Nothing>()
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val error: String?) : Result<Nothing>()
}