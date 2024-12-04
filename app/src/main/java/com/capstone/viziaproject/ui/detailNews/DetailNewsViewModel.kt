package com.capstone.viziaproject.ui.detailNews

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.viziaproject.data.pref.UserModel
import com.capstone.viziaproject.data.repository.NewsRepository
import com.capstone.viziaproject.data.repository.UserRepository
import com.capstone.viziaproject.data.response.DataNews
import kotlinx.coroutines.launch

class DetailNewsViewModel(
    private val userRepository: UserRepository,
    private val newsRepository: NewsRepository
) : ViewModel() {
    private val _detailNews = MutableLiveData<DataNews>()
    val detailNews: LiveData<DataNews> = _detailNews

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getSession(): LiveData<UserModel> = userRepository.getSession().asLiveData()

    fun getDetail(storyUrl: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = newsRepository.getDetailStory(storyUrl)
                if (response.status == "success") {
                    _detailNews.value = response.data
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
}