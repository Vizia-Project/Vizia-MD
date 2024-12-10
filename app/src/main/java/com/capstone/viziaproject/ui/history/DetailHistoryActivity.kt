package com.capstone.viziaproject.ui.history

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.capstone.viziaproject.R
import com.capstone.viziaproject.data.response.DataHistoryDetail
import com.capstone.viziaproject.databinding.ActivityDetailHistoryBinding
import com.capstone.viziaproject.ui.main.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetailHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailHistoryBinding

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detail = intent.getParcelableExtra<DataHistoryDetail>(EXTRA_HISTORY_DETAIL)
        if (detail != null) {
            displayDetails(detail)
        }

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

    @SuppressLint("SetTextI18n")
    private fun displayDetails(detail: DataHistoryDetail) {
        binding.apply {
            val answers = detail.questionResult
            if (answers.isNotEmpty()) {
                fun getAnswerText(value: Int): String = when (value) {
                    0 -> "Tidak"
                    1 -> "Ya"
                    else -> "Invalid"
                }

                tvCase1.text = getAnswerText(answers.getOrNull(0) ?: -1)
                tvCase2.text = getAnswerText(answers.getOrNull(1) ?: -1)
                tvCase3.text = getAnswerText(answers.getOrNull(2) ?: -1)
                tvCase4.text = getAnswerText(answers.getOrNull(3) ?: -1)
                tvCase5.text = getAnswerText(answers.getOrNull(4) ?: -1)
                tvCase6.text = getAnswerText(answers.getOrNull(5) ?: -1)
                tvCase7.text = getAnswerText(answers.getOrNull(6) ?: -1)
            }

            tvIndikasi.text = detail.predictionResult
            tvAkurasi.text = "${"%.2f".format(detail.accuracy)}%"
            tvMasalah.text = detail.infectionStatus + "Eye"
            tvKeterangan.text = HtmlCompat.fromHtml(detail.information, HtmlCompat.FROM_HTML_MODE_LEGACY)
            date.text = detail.date

            Glide.with(this@DetailHistoryActivity)
                .load(detail.image)
                .into(imagePlaceholder)
        }
    }

    private fun navigateToMainActivity(fragmentId: Int) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("FRAGMENT_ID", fragmentId)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }


    companion object {
        const val EXTRA_HISTORY_DETAIL = "extra_history_detail"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}