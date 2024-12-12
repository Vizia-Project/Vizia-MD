package com.capstone.viziaproject.ui.profil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.capstone.viziaproject.data.repository.UserRepository
import com.capstone.viziaproject.helper.Result
import kotlinx.coroutines.launch

class ProfilViewModel(private val userRepository: UserRepository) : ViewModel() {

    // Function to get user data by ID
    fun getUserProfile(userId: Int) = liveData {
        emit(Result.Loading)
        try {
            val response = userRepository.getUser(userId)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error("Failed to fetch profile: ${e.message}"))
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

}