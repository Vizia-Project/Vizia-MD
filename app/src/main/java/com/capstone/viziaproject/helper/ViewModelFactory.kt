package com.capstone.viziaproject.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.viziaproject.data.repository.HistoryRepository
import com.capstone.viziaproject.data.repository.NewsRepository
import com.capstone.viziaproject.data.repository.PredictRepository
import com.capstone.viziaproject.data.repository.UserRepository
import com.capstone.viziaproject.di.Injection
import com.capstone.viziaproject.ui.detailNews.DetailNewsViewModel
import com.capstone.viziaproject.ui.history.DetailHistoryViewModel
import com.capstone.viziaproject.ui.home.HomeViewModel
import com.capstone.viziaproject.ui.login.LoginViewModel
import com.capstone.viziaproject.ui.main.MainViewModel
import com.capstone.viziaproject.ui.news.NewsViewModel
import com.capstone.viziaproject.ui.profil.ProfilViewModel
import com.capstone.viziaproject.ui.register.RegisterViewModel
import com.capstone.viziaproject.ui.saveHistory.SaveViewModel
import com.capstone.viziaproject.ui.scan.ScanViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val userRepository: UserRepository,
    private val newsRepository: NewsRepository,
    private val predictRepository: PredictRepository,
    private val historyRepository: HistoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(userRepository, newsRepository) as T
            }
            modelClass.isAssignableFrom(DetailNewsViewModel::class.java) -> {
                DetailNewsViewModel(userRepository, newsRepository) as T
            }
            modelClass.isAssignableFrom(NewsViewModel::class.java) -> {
                NewsViewModel(userRepository, newsRepository) as T
            }
            modelClass.isAssignableFrom(ScanViewModel::class.java) -> {
                ScanViewModel(userRepository, predictRepository) as T
            }
            modelClass.isAssignableFrom(DetailHistoryViewModel::class.java) -> {
                DetailHistoryViewModel(userRepository, predictRepository, historyRepository) as T
            }
            modelClass.isAssignableFrom(SaveViewModel::class.java) -> {
                SaveViewModel(userRepository, historyRepository) as T
            }
            modelClass.isAssignableFrom(ProfilViewModel::class.java) -> {
                ProfilViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ViewModelFactory(
                    Injection.provideUserRepository(context),
                    Injection.provideNewsRepository(context),
                    Injection.providePredictRepository(context),
                    Injection.provideHistoryRepository(context)
                ).also {
                    INSTANCE = it
                }
            }
        }
    }
}