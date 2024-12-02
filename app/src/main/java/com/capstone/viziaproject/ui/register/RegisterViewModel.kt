package com.capstone.viziaproject.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.capstone.viziaproject.data.repository.UserRepository
import com.capstone.viziaproject.data.response.RegisterResponse
import com.capstone.viziaproject.helper.Result

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun signup(name: String, email: String, password: String, passwordConfirmation: String): LiveData<Result<RegisterResponse>> {
        return userRepository.register(name, email, password, passwordConfirmation)
    }
}