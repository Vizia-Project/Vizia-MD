package com.capstone.viziaproject.ui.saveHistory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.viziaproject.data.database.History
import com.capstone.viziaproject.data.pref.UserModel
import com.capstone.viziaproject.data.repository.HistoryRepository
import com.capstone.viziaproject.data.repository.PredictRepository
import com.capstone.viziaproject.data.repository.UserRepository
import com.capstone.viziaproject.data.response.DataHistoryDetail
import com.capstone.viziaproject.data.response.DataItemHistory
import com.capstone.viziaproject.helper.Result
import kotlinx.coroutines.launch

class SaveViewModel(private val userRepository: UserRepository, private val historyRepository: HistoryRepository) : ViewModel() {
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _getHistory = MutableLiveData<List<DataHistoryDetail>?>()
    val getHistory: LiveData<List<DataHistoryDetail>?> = _getHistory

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun clearError() {
        _error.value = null
    }

    fun getHistoryUser(userId: Int): LiveData<List<History?>> = historyRepository.getAllHistories(userId)
}