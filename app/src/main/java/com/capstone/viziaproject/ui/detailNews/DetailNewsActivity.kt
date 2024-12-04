package com.capstone.viziaproject.ui.detailNews

import android.content.Intent
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

class DetailNewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailNewsBinding
    private val viewModel by viewModels<DetailNewsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    companion object {
        const val STORY_URL = "STORY_URL"
    }

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
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                Log.e("cekcek123", it)
            }
        }
    }
}