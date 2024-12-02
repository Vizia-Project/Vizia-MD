package com.capstone.viziaproject.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.viziaproject.data.pref.UserModel
import com.capstone.viziaproject.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun saveSession(user: UserModel) {
        viewModelScope.launch { userRepository.saveSession(user) }
        Log.d("cekcek123", "Saving session: $user")
    }

    fun login(email: String, password: String) = userRepository.login(email, password)
}