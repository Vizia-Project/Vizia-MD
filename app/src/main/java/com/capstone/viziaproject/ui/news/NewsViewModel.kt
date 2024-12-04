package com.capstone.viziaproject.ui.news

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.viziaproject.data.pref.UserModel
import com.capstone.viziaproject.data.repository.NewsRepository
import com.capstone.viziaproject.data.repository.UserRepository
import com.capstone.viziaproject.data.response.DataItem
import com.capstone.viziaproject.helper.Result
import kotlinx.coroutines.launch

class NewsViewModel(private val userRepository: UserRepository, private val newsRepository: NewsRepository) : ViewModel() {

    private var _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: MutableLiveData<Uri?> = _currentImageUri

    private val _listNews = MutableLiveData<List<DataItem>>()
    val listNews: LiveData<List<DataItem>> = _listNews

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun fetchEvents() {
        getAllArticle()
        _error.value = null
    }

    fun getAllArticle() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                newsRepository.getNews().observeForever { result ->
                    when (result) {
                        is Result.Loading -> _isLoading.value = true
                        is Result.Success -> {
                            _isLoading.value = false
                            _listNews.value = result.data.data
                        }
                        is Result.Error -> {
                            _isLoading.value = false
                            handleError(result.error)
                        }
                    }
                }
            } catch (e: Exception) {
                _isLoading.value = false
                handleError(e.message)
            }
        }
    }

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }

    fun clearError() {
        _error.value = null
    }

    private fun getErrorMessage(statusCode: Int) {
        val errorMessage = when (statusCode) {
            401 -> "$statusCode: Bad Request"
            403 -> "$statusCode: Forbidden"
            404 -> "$statusCode: Not Found"
            else -> "$statusCode: An unexpected error occurred."
        }
        _error.value = errorMessage
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    private fun handleError(message: String?) {
        _error.value = message
        Log.e("cekcek", message ?: "Unknown error")
    }
}