package com.capstone.viziaproject.ui.scan

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone.viziaproject.data.pref.UserModel
import com.capstone.viziaproject.data.repository.PredictRepository
import com.capstone.viziaproject.data.repository.UserRepository
import java.io.File

class ScanViewModel(private val userRepository: UserRepository, private val predictRepository: PredictRepository) : ViewModel() {
    private var _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: MutableLiveData<Uri?> = _currentImageUri

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    fun store(
        imageUri: File,
        userId: Int,
        date: String,
        questionResult: List<Int>,
        infectionStatus: String,
        predictionResult: String,
        accuracy: Double,
        information: String
    ) = predictRepository.add(imageUri, userId, date, questionResult, infectionStatus, predictionResult, accuracy, information)


    fun clearError() {
        _error.value = null
    }

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }
    private fun handleError(message: String?) {
        _error.value = message
        Log.e("MainViewModel", message ?: "Unknown error")
    }
}