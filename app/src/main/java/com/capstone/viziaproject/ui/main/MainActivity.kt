package com.capstone.viziaproject.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.capstone.viziaproject.R
import com.capstone.viziaproject.data.pref.UserPreference
import com.capstone.viziaproject.data.pref.dataStore
import com.capstone.viziaproject.databinding.ActivityMainBinding
import com.capstone.viziaproject.helper.ViewModelFactory
import com.capstone.viziaproject.ui.IntroActivity
import com.capstone.viziaproject.ui.home.HomeFragment
import com.capstone.viziaproject.ui.news.NewsFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var pref: UserPreference
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_scan, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val fragmentId = intent.getIntExtra("FRAGMENT_ID", R.id.navigation_home)
        when (fragmentId) {
            R.id.navigation_home -> {
                navController.navigate(R.id.navigation_home)
            }
            R.id.navigation_notifications -> {
                navController.navigate(R.id.navigation_notifications)
            }
            R.id.navigation_scan -> {
                navController.navigate(R.id.navigation_scan)
            }
            else -> {
                navController.navigate(R.id.navigation_home)
            }
        }

        application?.let {
            pref = UserPreference.getInstance(it.dataStore)
        }

        viewModel.getSession().observe(this) { user ->
            Log.d("cekcek", "User session: token=${user.token}, isLogin=${user.isLogin}")
            if (user.token.isNotEmpty() && user.isLogin) {
                Log.d("cekcek", "Formatted token: Bearer ${user.token}")
//                val token = "Bearer ${user.token}"
            } else {
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}