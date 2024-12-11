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
import com.capstone.viziaproject.data.database.History
import com.capstone.viziaproject.data.response.DataHistoryDetail
import com.capstone.viziaproject.databinding.ActivityDetailHistoryBinding
import com.capstone.viziaproject.helper.ViewModelFactory
import com.capstone.viziaproject.ui.main.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

@Suppress("NAME_SHADOWING")
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

//        val eventId = intent.getIntExtra("EVENT_ID", -1)
//        if (eventId == -1) {
//            Toast.makeText(this, "Invalid Event ID", Toast.LENGTH_SHORT).show()
//            finish()
//            return
//        }
        val eventId = intent.getIntExtra("EVENT_ID", -1)

        setupObservers()
        fetchEventDetail(eventId)

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

        viewModel.checkSave(eventId)
        Log.d("cekceksave","$eventId")
        viewModel.getSession().observe(this) { userModel ->
            val userId = userModel.userId
            viewModel.isSave.observe(this) { favorite ->
//                val storyId = favorite!!.id
                if (favorite != null) {
                    viewModel.getEventDetailOrSave(userId, eventId)
                } else {
                    viewModel.getDetail(eventId)
                }
            }
        }
    }


    private fun setupObservers() {
//        viewModel.isLoading.observe(this) { isLoading ->
//            binding.contentGroup.visibility = if (isLoading) View.GONE else View.VISIBLE
//            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
//            binding.pgError.visibility = if (isLoading) View.VISIBLE else View.GONE
//        }

        viewModel.detailHistory.observe(this) { event ->
            event?.let { displayDetails(it) }
        }

        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let { showError(it) }
        }
        viewModel.getSession().observe(this) { userModel ->
            Log.d("cekcekquest7", "masukgetsession")
            if (userModel != null) {
                val userId = userModel.userId
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
        viewModel.saveAddedStatus.observe(this) { isAdded ->
            if (isAdded) {
                Toast.makeText(this, "Riwayat berhasil disimpan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal menyimpan riwayat", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.saveRemovedStatus.observe(this) { isRemoved ->
            if (isRemoved) {
                Toast.makeText(this, "Riwayat berhasil disimpan", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Gagal menyimpan riwayat", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isSave.observe(this) { favorite ->
            if (favorite != null) {
                binding.saveButton.apply {
                    text = "Saved"
                    setBackgroundColor(ContextCompat.getColor(this@DetailHistoryActivity, R.color.purple_move))
                    setTextColor(ContextCompat.getColor(this@DetailHistoryActivity, R.color.white))
                }
            } else {
                binding.saveButton.apply {
                    text = "Save"
                    setBackgroundColor(ContextCompat.getColor(this@DetailHistoryActivity, R.color.purple_200))
                    setTextColor(ContextCompat.getColor(this@DetailHistoryActivity, R.color.black))
                }
            }
        }
    }

    private fun fetchEventDetail(eventId: Int) {
        viewModel.getDetail(eventId)
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