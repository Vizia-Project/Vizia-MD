package com.capstone.viziaproject.ui.history

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract.Instances.EVENT_ID
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.capstone.viziaproject.R
import com.capstone.viziaproject.data.response.DataHistoryDetail
import com.capstone.viziaproject.databinding.ActivityDetailHistoryBinding
import com.capstone.viziaproject.helper.ViewModelFactory

@Suppress("DEPRECATION")
class DetailHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailHistoryBinding
    private var isToastShown = false
//    private var userId = -1

    private val viewModel: DetailHistoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val detail = intent.getParcelableExtra<DataHistoryDetail>(EXTRA_HISTORY_DETAIL)
        if (detail != null) {
            displayDetails(detail)
        }

//        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
//        bottomNavigationView.selectedItemId = R.id.navigation_home
//
//        bottomNavigationView.setOnItemSelectedListener { item ->
//            when (item.itemId) {
//                R.id.navigation_home -> {
//                    navigateToMainActivity(R.id.navigation_home)
//                    true
//                }
//
//                R.id.navigation_save -> {
//                    navigateToMainActivity(R.id.navigation_save)
//                    true
//                }
//
//                else -> false
//            }
//        }

//        binding.fabScan.setOnClickListener {
//            val navViewScan = findViewById<BottomNavigationView>(R.id.nav_view)
//
//            navViewScan.menu.setGroupCheckable(0, false, false)
//            navViewScan.menu.findItem(R.id.navigation_home).isChecked = false
//            navViewScan.menu.findItem(R.id.navigation_save).isChecked = false
//            navigateToMainActivity(R.id.navigation_scan)
//        }

        val eventId = intent.getIntExtra("EVENT_ID", -1)
        if (eventId == -1) {
            Toast.makeText(this, "Invalid Event ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        setupObservers()
        fetchEventDetail(eventId)

        Log.d("cekceksave","$eventId")
        viewModel.getSession().observe(this) { userModel ->
            val userId = userModel.userId
            viewModel.checkSave(eventId)
            viewModel.isSave.observe(this) { favorite ->
//                val storyId = favorite!!.id
                if (favorite != null) {
                    viewModel.getEventDetailOrSave(eventId)
                } else {
                    viewModel.getDetail(eventId)
                }
            }
            viewModel.isSave.observe(this) { favorite ->
                binding.saveButton.apply {
                    if (favorite != null) {
                        text = "Batalkan Simpan Riwayat"
                        setBackgroundColor(ContextCompat.getColor(this@DetailHistoryActivity, R.color.purple_move))
                        setTextColor(ContextCompat.getColor(this@DetailHistoryActivity, R.color.white))
                    }else {
                        text = "Simpan Riwayat"
                        setBackgroundColor(ContextCompat.getColor(this@DetailHistoryActivity, R.color.purple_200))
                        setTextColor(ContextCompat.getColor(this@DetailHistoryActivity, R.color.black))
                    }
                }

            }

            binding.saveButton.setOnClickListener {
                val event = viewModel.detailHistory.value
                event?.let { historyEvent ->
                    if (viewModel.isSave.value == null) {
                        viewModel.addEventToSave(userId, historyEvent)
                    } else {
                        viewModel.removeEventFromSave(userId, historyEvent)
                    }
                }
            }
        }
    }


    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.detailHistory.observe(this) { event ->
            event?.let { displayDetails(it) }
        }

        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let { showError(it) }
        }
        viewModel.saveAddedStatus.observe(this) { isAdded ->
            if (isAdded) {
                Toast.makeText(this, "Riwayat berhasil disimpan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal menyimpan riwayat", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.saveRemovedStatus.observe(this) { isRemoved ->
            if (isRemoved) {
                Toast.makeText(this, "Riwayat berhasil dihapus", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal menghapus riwayat", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchEventDetail(eventId: Int) {
        viewModel.getSession().observe(this) { userModel ->
            val userId = userModel.userId
            viewModel.getEventDetailOrSave(eventId)
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
            tvMasalah.text = detail.infectionStatus
            tvKeterangan.text = HtmlCompat.fromHtml(detail.information, HtmlCompat.FROM_HTML_MODE_LEGACY)
            date.text = detail.date

            Glide.with(this@DetailHistoryActivity)
                .load(detail.image)
                .into(imagePlaceholder)
        }
    }

//    private fun navigateToMainActivity(fragmentId: Int) {
//        val intent = Intent(this, MainActivity::class.java)
//        intent.putExtra("FRAGMENT_ID", fragmentId)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
//        startActivity(intent)
//        finish()
//    }

    private fun showError(message: String) {
        if (!isToastShown) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            isToastShown = true
            viewModel.clearError()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    companion object {
        const val EXTRA_HISTORY_DETAIL = "extra_history_detail"
        const val EVENT_ID = "event_id"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}