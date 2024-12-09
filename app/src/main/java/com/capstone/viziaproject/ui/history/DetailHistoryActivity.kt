package com.capstone.viziaproject.ui.history

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.capstone.viziaproject.data.response.DataHistoryDetail
import com.capstone.viziaproject.databinding.ActivityDetailHistoryBinding

class DetailHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detail = intent.getParcelableExtra<DataHistoryDetail>(EXTRA_HISTORY_DETAIL)
        if (detail != null) {
            displayDetails(detail)
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

    companion object {
        const val EXTRA_HISTORY_DETAIL = "extra_history_detail"
    }
}