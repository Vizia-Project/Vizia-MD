package com.capstone.viziaproject.ui.scan

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone.viziaproject.data.pref.UserModel
import com.capstone.viziaproject.data.repository.PredictRepository
import com.capstone.viziaproject.data.repository.UserRepository
import com.capstone.viziaproject.data.response.DataItem
import com.capstone.viziaproject.data.response.StoreHistoryResponse
import com.capstone.viziaproject.helper.Result
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class DiagnosisViewModel(private val userRepository: UserRepository, private val predictRepository: PredictRepository) : ViewModel() {

    private var _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> = _currentImageUri

    private val _listNews = MutableLiveData<List<DataItem>>()
    val listNews: LiveData<List<DataItem>> = _listNews

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
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


    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }
}
