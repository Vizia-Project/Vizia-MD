package com.capstone.viziaproject.ui.history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.viziaproject.data.pref.UserModel
import com.capstone.viziaproject.data.repository.PredictRepository
import com.capstone.viziaproject.data.repository.UserRepository
import com.capstone.viziaproject.data.response.DataHistoryDetail
import com.capstone.viziaproject.data.response.DataNews
import kotlinx.coroutines.launch

class DetailHistoryViewModel(
    private val userRepository: UserRepository,
    private val predictRepository: PredictRepository
) : ViewModel() {
    private val _detailHistory = MutableLiveData<DataHistoryDetail>()
    val detailHistory: LiveData<DataHistoryDetail> = _detailHistory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getSession(): LiveData<UserModel> = userRepository.getSession().asLiveData()

    fun getDetail(id: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = predictRepository.getDetailStory(id)
                if (response.status == "success") {
                    _detailHistory.value = response.data
                } else {
                    _error.value = response.message
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("cekcek123", "Error fetching story detail: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}