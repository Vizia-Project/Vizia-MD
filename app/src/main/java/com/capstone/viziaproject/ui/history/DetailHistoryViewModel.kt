package com.capstone.viziaproject.ui.history

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
import kotlinx.coroutines.launch

class DetailHistoryViewModel(
    private val userRepository: UserRepository,
    private val predictRepository: PredictRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _isSave = MutableLiveData<History?>()
    val isSave: LiveData<History?> get() = _isSave

    private val _saveAddedStatus = MutableLiveData<Boolean>()
    val saveAddedStatus: LiveData<Boolean> = _saveAddedStatus

    private val _saveRemovedStatus = MutableLiveData<Boolean>()
    val saveRemovedStatus: LiveData<Boolean> = _saveRemovedStatus

    private val _detailHistory = MutableLiveData<DataHistoryDetail>()
    val detailHistory: LiveData<DataHistoryDetail> = _detailHistory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error


    fun getSession(): LiveData<UserModel> = userRepository.getSession().asLiveData()

    fun addEventToSave(userId: Int, history: DataHistoryDetail) {
        val questionResults = (0..6).map { index ->
            history.questionResult.getOrNull(index) ?: -1
        }

        val historyEntity = History(
            id = history.id,
            userId = userId,
            date = history.date,
            image = history.image,
            infectionStatus = history.infectionStatus,
            q1 = questionResults.getOrNull(0),
            q2 = questionResults.getOrNull(1),
            q3 = questionResults.getOrNull(2),
            q4 = questionResults.getOrNull(3),
            q5 = questionResults.getOrNull(4),
            q6 = questionResults.getOrNull(5),
            q7 = questionResults.getOrNull(6),
            predictionResult = history.predictionResult,
            accuracy = history.accuracy,
            information = history.information
        )
        viewModelScope.launch {
            try {
                historyRepository.insert(historyEntity)
                _isSave.value = historyEntity
                _saveAddedStatus.value = true
            } catch (e: Exception) {
                _saveAddedStatus.value = false
                _error.value = e.localizedMessage ?: "An unexpected error occurred"
            }
        }
    }

    fun removeEventFromSave(userId: Int, history: DataHistoryDetail) {
        val questionResults = (0..6).map { index ->
            history.questionResult.getOrNull(index) ?: -1
        }

        val historyEntity = History(
            id = history.id,
            userId = userId,
            date = history.date,
            image = history.image,
            infectionStatus = history.infectionStatus,
            q1 = questionResults.getOrNull(0),
            q2 = questionResults.getOrNull(1),
            q3 = questionResults.getOrNull(2),
            q4 = questionResults.getOrNull(3),
            q5 = questionResults.getOrNull(4),
            q6 = questionResults.getOrNull(5),
            q7 = questionResults.getOrNull(6),
            predictionResult = history.predictionResult,
            accuracy = history.accuracy,
            information = history.information
        )
        viewModelScope.launch {
            try {
                historyRepository.delete(historyEntity)
                _isSave.value = null
                _saveRemovedStatus.value = true
            } catch (e: Exception) {
                _saveRemovedStatus.value = false
                _error.value = e.localizedMessage ?: "An unexpected error occurred"
            }
        }
    }


    fun getEventDetailOrSave(id: Int) {
        viewModelScope.launch {
            val history = historyRepository.getHistoryEventById(id)
                if (history != null) {
                    Log.d("cekcekhistory","historyy adaa")
                    _detailHistory.value = DataHistoryDetail(
                        date = history.date ?: "No Date",
                        image = history.image ?: "",
                        infectionStatus = history.infectionStatus ?: "Unknown",
                        questionResult = listOf(
                            history.q1,
                            history.q2,
                            history.q3,
                            history.q4,
                            history.q5,
                            history.q6,
                            history.q7
                        ),
                        predictionResult = history.predictionResult ?: "No Result",
                        accuracy = history.accuracy,
                        information = history.information ?: "No Info",
                        id = history.id,
                    )
                } else {
                    Log.d("cekcekhistory","historyy null")
                    getDetail(id)
                }
            }

    }

    fun checkSave(eventId: Int) {
        viewModelScope.launch {
            try {
                val savedEvent = historyRepository.getHistoryEventById(eventId)
                _isSave.value = savedEvent
            } catch (e: Exception) {
                _isSave.value = null
                _error.value = e.localizedMessage ?: "An unexpected error occurred"
            }
        }
    }

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