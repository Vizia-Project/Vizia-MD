package com.capstone.viziaproject.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone.viziaproject.data.pref.UserModel
import com.capstone.viziaproject.data.repository.UserRepository

class HomeViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text
    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }
}