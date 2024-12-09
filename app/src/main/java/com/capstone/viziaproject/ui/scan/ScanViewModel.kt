package com.capstone.viziaproject.ui.scan

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.viziaproject.data.pref.UserModel
import com.capstone.viziaproject.data.repository.PredictRepository
import com.capstone.viziaproject.data.repository.UserRepository
import com.capstone.viziaproject.data.response.DataItemHistory
import kotlinx.coroutines.launch
import com.capstone.viziaproject.helper.Result
import java.io.File

class ScanViewModel(private val userRepository: UserRepository, private val predictRepository: PredictRepository) : ViewModel() {
    private var _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: MutableLiveData<Uri?> = _currentImageUri

    private val _getHistory = MutableLiveData<List<DataItemHistory>?>()
    val getHistory: LiveData<List<DataItemHistory>?> = _getHistory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }


    fun store(
        userId: Int,
        date: String,
        image: File,
        questionResult: List<Int>,
        infectionStatus: String,
        predictionResult: String,
        accuracy: Double,
        information: String
    )= predictRepository.add(userId, date, image, questionResult, infectionStatus, predictionResult, accuracy, information)


    fun clearError() {
        _error.value = null
    }

    fun getHistoryUser(userId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                predictRepository.getDataHistory(userId).observeForever { result ->
                    when (result) {
                        is Result.Loading -> {
                            _isLoading.value = true
                        }
                        is Result.Success -> {
                            _getHistory.value = result.data.data
                            _isLoading.value = false
                        }
                        is Result.Error -> {
                            _error.value = result.error
                            _isLoading.value = false
                        }
                    }
                }
            }catch (e: Exception) {
                _isLoading.value = false
                handleError(e.message)
            }
        }
    }

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }
    private fun handleError(message: String?) {
        _error.value = message
        Log.e("MainViewModel", message ?: "Unknown error")
    }
}