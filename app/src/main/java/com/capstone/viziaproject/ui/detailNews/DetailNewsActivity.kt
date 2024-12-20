package com.capstone.viziaproject.ui.detailNews

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.capstone.viziaproject.R
import com.capstone.viziaproject.databinding.ActivityDetailNewsBinding
import com.capstone.viziaproject.helper.ViewModelFactory
import com.capstone.viziaproject.ui.IntroActivity
import com.capstone.viziaproject.ui.main.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetailNewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailNewsBinding
    private var isToastShown = false
    private val viewModel by viewModels<DetailNewsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        const val STORY_URL = "STORY_URL"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }


    @SuppressLint("CutPasteId")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyUrl = intent.getStringExtra(STORY_URL)
        if (storyUrl.isNullOrEmpty()) {
            Log.e("cekcek123detail", "storyUrl null atau kosong")
            Toast.makeText(this, "Data cerita tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (isInternetAvailable()) {
            binding.pgError.visibility = View.GONE
            binding.contentGroup.visibility = View.VISIBLE
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            binding.pgError.visibility = View.VISIBLE
            binding.contentGroup.visibility = View.GONE
        }

        viewModel.getSession().observe(this) { user ->
            Log.d("cekcek123detail", "User session: token=${user.token}, isLogin=${user.isLogin}")
            if (user.token.isNotEmpty() && user.isLogin) {
                Log.d("cekcek123detail", "Formatted token: Bearer ${user.token}")
                val token = "Bearer ${user.token}"
                viewModel.getDetail(storyUrl)
                Log.d("cekcek123", "Memuat detail cerita dengan ID: $storyUrl")
            } else {
                startActivity(Intent(this, IntroActivity::class.java))
                finish()
            }
        }

        viewModel.detailNews.observe(this) { story ->
            binding.apply {
                Glide.with(this@DetailNewsActivity)
                    .load(story.image)
                    .into(imgEvent)
                tvName.text = story.title
                binding.deskripsi.text = Html.fromHtml(story.content, Html.FROM_HTML_MODE_COMPACT)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.contentGroup.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.imgEvent.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.tvName.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.deskripsi.visibility = if (isLoading) View.GONE else View.VISIBLE
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.pgError.visibility = if (isLoading) View.GONE else View.GONE
        }

//        viewModel.error.observe(this) { errorMessage ->
//            errorMessage?.let { showError(it) }
//        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.selectedItemId = R.id.navigation_home

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navigateToMainActivity(R.id.navigation_home)
                    true
                }

                R.id.navigation_save -> {
                    navigateToMainActivity(R.id.navigation_save)
                    true
                }

                else -> false
            }
        }

        binding.fabScan.setOnClickListener {
            val navViewScan = findViewById<BottomNavigationView>(R.id.nav_view)

            navViewScan.menu.setGroupCheckable(0, false, false)
            navViewScan.menu.findItem(R.id.navigation_home).isChecked = false
            navViewScan.menu.findItem(R.id.navigation_save).isChecked = false
            navigateToMainActivity(R.id.navigation_scan)
        }

    }

    private fun navigateToMainActivity(fragmentId: Int) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("FRAGMENT_ID", fragmentId)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

//    private fun showError(message: String) {
//        if (!isToastShown) {
//            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//            isToastShown = true
//            viewModel.clearError()
//        }
//    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}