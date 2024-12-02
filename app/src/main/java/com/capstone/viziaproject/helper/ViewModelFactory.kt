package com.capstone.viziaproject.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.viziaproject.data.pref.UserPreference
import com.capstone.viziaproject.data.repository.UserRepository
import com.capstone.viziaproject.di.Injection
import com.capstone.viziaproject.ui.main.MainViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repository: UserRepository,
    private val pref: UserPreference
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    val userRepository = Injection.provideRepository(context)
                    val userPreference = Injection.providePreference(context)
                    INSTANCE = ViewModelFactory(userRepository, userPreference)
                }
            }
            return INSTANCE ?: throw IllegalStateException("ViewModelFactory should not be null")
        }
    }
}