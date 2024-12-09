package com.capstone.viziaproject.ui.scan

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.capstone.viziaproject.R
import com.capstone.viziaproject.databinding.ActivityDiagnosisBinding
import com.capstone.viziaproject.ui.main.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class DiagnosisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiagnosisBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiagnosisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dateTime = intent.getStringExtra("dateTime") ?: "Waktu tidak tersedia"
        val answers = intent.getIntegerArrayListExtra("answers") ?: arrayListOf()
        val imageUri = intent.getStringExtra("imageUriQuest")?.let { Uri.parse(it) }
        val label = intent.getStringExtra("label")
        val diagnosisResult = intent.getStringExtra("diagnosisResult")
        val confidence = intent.getFloatExtra("confidence", 0.0f)
        val accuracy = intent.getFloatExtra("accuracy", 0.0f)

        binding.imagePlaceholder.setImageURI(imageUri)
        binding.date.text = dateTime
        val resultDisease = when (diagnosisResult) {
            "Negative" -> {
                "Label 0"
            }
            "Positive" -> {
                label
            }
            else -> {
                "Unknown"
            }
        }

        val condition = when (resultDisease) {
            "Label 0" -> "Normal Eye"
            "Label 1" -> "Stye / Hordeulum Eye"
            "Label 2" -> "Uveitis Eye"
            else -> "Unknown"
        }

        binding.tvMasalah.text = "$diagnosisResult Eye Disease"
        binding.tvAkurasi.text = if (diagnosisResult == "Negative") {
            val adjustedAccuracy = 1.0f - accuracy
            "${"%.2f".format(adjustedAccuracy * 100)}%"
        } else {
            "${"%.2f".format(accuracy * 100)}%"
        }

        binding.tvIndikasi.text = "($condition)"
        Log.d("cekcekdiagnosis", "Akurasi pada gambar saja: ${"%.2f".format(confidence * 100)}%")

        if (answers.isNotEmpty()) {
            fun getAnswerText(value: Int): String = when (value) {
                0 -> "Tidak"
                1 -> "Ya"
                else -> "Invalid"
            }

            binding.tvCase1.text = getAnswerText(answers.getOrNull(0) ?: -1)
            binding.tvCase2.text = getAnswerText(answers.getOrNull(1) ?: -1)
            binding.tvCase3.text = getAnswerText(answers.getOrNull(2) ?: -1)
            binding.tvCase4.text = getAnswerText(answers.getOrNull(3) ?: -1)
            binding.tvCase5.text = getAnswerText(answers.getOrNull(4) ?: -1)
            binding.tvCase6.text = getAnswerText(answers.getOrNull(5) ?: -1)
            binding.tvCase7.text = getAnswerText(answers.getOrNull(6) ?: -1)

            val htmlContent = when (resultDisease) {
                "Label 0" -> """
                <h2>Mata Normal</h2>
                <p>Mata Anda dalam kondisi normal. Berikut adalah beberapa tips untuk menjaga kesehatan mata Anda:</p>
                <ul>
                    <li>Konsumsi makanan kaya vitamin A, C, dan E seperti wortel dan sayuran hijau.</li>
                    <li>Jaga kebersihan area sekitar mata.</li>
                    <li>Hindari menatap layar komputer atau ponsel terlalu lama tanpa istirahat.</li>
                    <li>Gunakan kacamata pelindung saat berada di bawah sinar matahari yang terik.</li>
                </ul>
            """.trimIndent()

                "Label 1" -> """
                <h2>Stye Eye</h2>
                <p><b>Pengertian:</b> Stye atau bintitan adalah infeksi pada kelenjar di sekitar kelopak mata yang biasanya disebabkan oleh bakteri.</p>
                <p><b>Gejala:</b></p>
                <ul>
                    <li>Benjolan kecil dan merah di kelopak mata.</li>
                    <li>Pembengkakan, nyeri, dan iritasi di sekitar mata.</li>
                    <li>Keluar nanah dari benjolan.</li>
                </ul>
                <p><b>Cara Mengatasi:</b></p>
                <ul>
                    <li>Kompres hangat selama 10-15 menit beberapa kali sehari.</li>
                    <li>Hindari memencet atau menyentuh benjolan.</li>
                    <li>Gunakan obat tetes mata antibiotik sesuai anjuran dokter.</li>
                </ul>
                <p><b>Cara Mencegah:</b></p>
                <ul>
                    <li>Selalu cuci tangan sebelum menyentuh mata.</li>
                    <li>Jangan menggunakan produk kosmetik mata yang sudah kadaluwarsa.</li>
                    <li>Hindari berbagi handuk atau alat rias mata dengan orang lain.</li>
                </ul>
            """.trimIndent()

                "Label 2" -> """
                <h2>Uveitis Eye</h2>
                <p><b>Pengertian:</b> Uveitis adalah peradangan pada lapisan tengah mata (uvea), yang dapat menyebabkan gangguan penglihatan jika tidak segera diatasi.</p>
                <p><b>Gejala:</b></p>
                <ul>
                    <li>Kemerahan pada mata.</li>
                    <li>Penglihatan buram atau kabur.</li>
                    <li>Nyeri pada mata, terutama saat terkena cahaya terang.</li>
                </ul>
                <p><b>Cara Mengatasi:</b></p>
                <ul>
                    <li>Konsultasi ke dokter mata untuk mendapatkan obat tetes mata steroid atau antiinflamasi.</li>
                    <li>Hindari stres yang dapat memperburuk peradangan.</li>
                </ul>
                <p><b>Cara Mencegah:</b></p>
                <ul>
                    <li>Lindungi mata dari cedera atau infeksi.</li>
                    <li>Hindari paparan bahan kimia atau iritan.</li>
                    <li>Jaga kebersihan mata secara rutin.</li>
                </ul>
            """.trimIndent()

                else -> """
                <h2>Tidak Diketahui</h2>
                <p>Mohon maaf, kami tidak dapat menentukan kondisi mata Anda.</p>
                <p>Silakan konsultasikan lebih lanjut dengan dokter mata untuk diagnosis dan pengobatan yang tepat.</p>
            """.trimIndent()
            }

            binding.tvKeterangan.text = HtmlCompat.fromHtml(htmlContent, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.selectedItemId = R.id.navigation_scan

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    showUnsavedDialog {
                        navigateToMainActivity(R.id.navigation_home)
                    }
                    true
                }

                R.id.navigation_save -> {
                    showUnsavedDialog {
                        navigateToMainActivity(R.id.navigation_save)
                    }
                    true
                }

                R.id.navigation_scan -> true
                else -> false
            }
        }
    }

    private fun showUnsavedDialog(onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Peringatan")
            .setMessage("Apakah Anda tidak ingin menyimpan hasil diagnosis?")
            .setPositiveButton("Ya") { _: DialogInterface, _: Int -> onConfirm() }
            .setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun navigateToMainActivity(fragmentId: Int) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("FRAGMENT_ID", fragmentId)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
