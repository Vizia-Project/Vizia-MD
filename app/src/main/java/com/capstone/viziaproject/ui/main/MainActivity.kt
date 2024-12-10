package com.capstone.viziaproject.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
                R.id.navigation_home, R.id.navigation_save
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val fragmentId = intent.getIntExtra("FRAGMENT_ID", R.id.navigation_home)
        if (fragmentId != R.id.navigation_home) {
            navController.navigate(fragmentId)
        }

        navView.setOnItemSelectedListener { item ->
            Log.d("BottomNav", "Clicked: ${item.itemId}")
            when (item.itemId) {
                R.id.navigation_home -> {
                    Log.d("BottomNav", "Navigating to Home")
                    navController.navigate(R.id.navigation_home)
                    true
                }
                R.id.navigation_save -> {
                    Log.d("BottomNav", "Navigating to Save")
                    navController.navigate(R.id.navigation_save)
                    true
                }
                else -> false
            }
        }


        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isScanFragment = destination.id == R.id.navigation_scan
            if (isScanFragment) {
                binding.navView.menu.setGroupCheckable(0, false, false)
            } else {
                binding.navView.menu.setGroupCheckable(0, true, true)
                binding.navView.menu.findItem(destination.id)?.isChecked = true
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

        binding.fabScan.setOnClickListener {
            val navControllerScan = findNavController(R.id.fragment_container)
            navControllerScan.navigate(R.id.navigation_scan)
            val navViewScan = findViewById<BottomNavigationView>(R.id.nav_view)

            navViewScan.menu.setGroupCheckable(0, false, false)
            navViewScan.menu.findItem(R.id.navigation_home).isChecked = false
            navViewScan.menu.findItem(R.id.navigation_save).isChecked = false
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun updateBottomNavigationStateForScanFragment() {
        binding.navView.menu.setGroupCheckable(0, false, false)
        binding.navView.menu.findItem(R.id.navigation_home).isChecked = false
        binding.navView.menu.findItem(R.id.navigation_save).isChecked = false
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}