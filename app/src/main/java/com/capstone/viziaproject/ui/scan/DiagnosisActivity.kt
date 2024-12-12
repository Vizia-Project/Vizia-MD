package com.capstone.viziaproject.ui.scan

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.capstone.viziaproject.R
import com.capstone.viziaproject.data.response.DataHistoryDetail
import com.capstone.viziaproject.databinding.ActivityDiagnosisBinding
import com.capstone.viziaproject.helper.ViewModelFactory
import com.capstone.viziaproject.ui.history.DetailHistoryViewModel
import com.capstone.viziaproject.ui.main.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

@Suppress("CAST_NEVER_SUCCEEDS")
class DiagnosisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiagnosisBinding
    private var isToastShown = false
    private val viewModel: DetailHistoryViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    @SuppressLint("SetTextI18n", "CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiagnosisBinding.inflate(layoutInflater)
        setContentView(binding.root)

        (this as? MainActivity)?.updateBottomNavigationStateForScanFragment()

        val dateTime = intent.getStringExtra("dateTime") ?: "Waktu tidak tersedia"
        val answers = intent.getIntegerArrayListExtra("answers") ?: arrayListOf()
        val imageUri = intent.getStringExtra("imageUriQuest")?.let { Uri.parse(it) }
        val label = intent.getStringExtra("label")
        val diagnosisResult = intent.getStringExtra("diagnosisResult")
        val confidence = intent.getFloatExtra("confidence", 0.0f)
        val accuracy = intent.getFloatExtra("accuracy", 0.0f)

        binding.imagePlaceholder.setImageURI(imageUri)
        binding.date.text = dateTime
        var resultDisease = when (diagnosisResult) {
            "Mata Normal" -> {
                "Label 0"
            }
            "Mata Tidak Normal" -> {
                label
            }
            else -> {
                "Tidak Diketahui"
            }
        }

        if (diagnosisResult == "Mata Tidak Normal" && resultDisease == "Label 0"){
            resultDisease = "Tidak Diketahui"
        }

        val condition = when (resultDisease) {
            "Label 0" -> "Mata Normal"
            "Label 1" -> "Mata Hordeulum (Bintitan)"
            "Label 2" -> "Mata Uveitis"
            else -> "Tidak Diketahui"
        }

        binding.tvMasalah.text = diagnosisResult
        binding.tvAkurasi.text = if (diagnosisResult == "Mata Normal") {
            val adjustedAccuracy = 1.0f - accuracy
            "${"%.2f".format(adjustedAccuracy * 100)}%"
        } else {
            "${"%.2f".format(accuracy * 100)}%"
        }
        Log.d("cekcekdiag", "Condition: $condition, Accuracy: $accuracy, Confidence: $confidence")

        binding.tvIndikasi.text = condition
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
                "Label 0" -> """<div style="font-family: Arial, sans-serif; line-height: 1.6; margin: 20px; padding: 0;"><h2 style=" color: #2c3e50; margin-bottom: 10px;">Mata Normal</h2><p style="text-align: justify; margin-bottom: 15px;">Mata Anda dalam kondisi normal. Menjaga kesehatan mata adalah langkah penting untuk memastikan penglihatan tetap optimal sepanjang hidup Anda. Berikut adalah beberapa tips yang dapat membantu Anda merawat mata agar tetap sehat.</p><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Konsumsi makanan kaya vitamin A, C, dan E seperti wortel, sayuran hijau, dan buah-buahan segar.</li><li style="margin-bottom: 10px;">Minum cukup air untuk menjaga kelembapan mata dan mencegah mata kering.</li><li style="margin-bottom: 10px;">Jaga kebersihan area sekitar mata, terutama jika Anda menggunakan riasan mata.</li><li style="margin-bottom: 10px;">Hindari menatap layar komputer atau ponsel terlalu lama tanpa istirahat. Terapkan aturan 20-20-20: setiap 20 menit, alihkan pandangan ke objek yang berjarak 20 kaki selama 20 detik.</li><li style="margin-bottom: 10px;">Gunakan kacamata pelindung saat berada di bawah sinar matahari yang terik untuk melindungi mata dari sinar UV.</li><li style="margin-bottom: 10px;">Rutin memeriksakan mata ke dokter untuk memastikan kondisi mata tetap sehat.</li></ul></div>""".trimIndent()
                "Label 1" -> """<div style="font-family: Arial, sans-serif; line-height: 1.6; margin: 20px; padding: 20px; background: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);"><h2 style="color: #2c3e50; margin-bottom: 10px;">Mata Bintitan (Hordeulum/Stye)</h2><p style="text-align: justify; margin-bottom: 15px;"><b>Pengertian:</b> Stye atau bintitan adalah infeksi pada kelenjar di sekitar kelopak mata yang biasanya disebabkan oleh bakteri, seperti *Staphylococcus aureus*. Kondisi ini umum terjadi dan sering kali tidak berbahaya, tetapi dapat menyebabkan ketidaknyamanan.</p><h3 style="color: #2c3e50; margin-bottom: 10px;">Gejala</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Benjolan kecil, merah, dan menyakitkan di kelopak mata.</li><li style="margin-bottom: 10px;">Pembengkakan, nyeri, dan iritasi di sekitar area mata.</li><li style="margin-bottom: 10px;">Keluar nanah atau cairan dari benjolan tersebut.</li><li style="margin-bottom: 10px;">Rasa gatal atau sensasi mengganjal pada mata.</li></ul><h3 style="color: #2c3e50; margin-bottom: 10px;">Cara Mengatasi</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Kompres hangat selama 10-15 menit beberapa kali sehari untuk membantu mengurangi peradangan.</li><li style="margin-bottom: 10px;">Hindari memencet atau menyentuh benjolan untuk mencegah infeksi menyebar.</li><li style="margin-bottom: 10px;">Gunakan obat tetes mata atau salep antibiotik sesuai anjuran dokter jika diperlukan.</li><li style="margin-bottom: 10px;">Jika gejala tidak membaik dalam beberapa hari, segera konsultasikan dengan dokter spesialis mata.</li></ul><h3 style="color: #2c3e50; margin-bottom: 10px;">Cara Mencegah</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Cuci tangan secara rutin sebelum menyentuh area mata untuk menghindari bakteri masuk.</li><li style="margin-bottom: 10px;">Jangan menggunakan produk kosmetik mata yang sudah kadaluwarsa atau milik orang lain.</li><li style="margin-bottom: 10px;">Hindari berbagi handuk, bantal, atau alat rias dengan orang lain.</li><li style="margin-bottom: 10px;">Bersihkan riasan mata sebelum tidur untuk mencegah penumpukan kotoran di sekitar kelopak mata.</li></ul><p style="text-align: justify; margin-bottom: 15px;">Menjaga kebersihan dan mengadopsi kebiasaan sehat sangat penting untuk mencegah stye. Jika Anda mengalami gejala berulang, konsultasikan dengan dokter untuk evaluasi lebih lanjut.</p><h3 style="color: #2c3e50; margin-bottom: 10px;">Langkah Selanjutnya</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Segera jadwalkan konsultasi dengan dokter mata untuk pemeriksaan menyeluruh.</li><li style="margin-bottom: 10px;">Pastikan untuk menyampaikan semua gejala yang Anda alami secara detail kepada dokter.</li><li style="margin-bottom: 10px;">Bawa catatan medis atau riwayat kesehatan sebelumnya yang relevan, jika ada.</li><li style="margin-bottom: 10px;">Ikuti panduan dokter untuk langkah diagnostik atau pengobatan selanjutnya, seperti tes lanjutan atau penggunaan alat medis tertentu.</li></ul></div>""".trimIndent()
                "Label 2" -> """<div style="font-family: Arial, sans-serif; line-height: 1.6; margin: 20px; padding: 20px; background: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);"><h2 style="color: #2c3e50; margin-bottom: 10px;">Uveitis Eye</h2><p style="text-align: justify; margin-bottom: 15px;"><b>Pengertian:</b> Uveitis adalah peradangan pada lapisan tengah mata, yang dikenal sebagai uvea. Uvea adalah bagian mata yang berfungsi penting dalam menyuplai darah ke retina. Kondisi ini dapat memengaruhi satu atau kedua mata, dengan tingkat keparahan yang bervariasi. Jika tidak segera diobati, uveitis dapat menyebabkan komplikasi serius seperti glaukoma, katarak, atau bahkan kehilangan penglihatan.</p><h3 style="color: #2c3e50; margin-bottom: 10px;">Gejala</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Kemerahan pada mata yang sering kali disertai peradangan.</li><li style="margin-bottom: 10px;">Penglihatan buram atau kabur yang dapat memburuk seiring waktu.</li><li style="margin-bottom: 10px;">Nyeri pada mata, terutama saat terkena cahaya terang (fotofobia).</li><li style="margin-bottom: 10px;">Munculnya floaters (bayangan kecil yang tampak mengapung dalam penglihatan).</li><li style="margin-bottom: 10px;">Penurunan penglihatan yang tiba-tiba pada beberapa kasus akut.</li></ul><h3 style="color: #2c3e50; margin-bottom: 10px;">Cara Mengatasi</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Segera konsultasi dengan dokter mata untuk diagnosis dan pengobatan yang tepat.</li><li style="margin-bottom: 10px;">Penggunaan obat tetes mata yang mengandung steroid atau antiinflamasi sesuai resep dokter.</li><li style="margin-bottom: 10px;">Jika disebabkan oleh infeksi, dokter mungkin akan meresepkan antibiotik atau antivirus.</li><li style="margin-bottom: 10px;">Mengelola stres dan menjaga pola hidup sehat untuk mempercepat pemulihan.</li><li style="margin-bottom: 10px;">Pada kasus yang lebih parah, mungkin diperlukan injeksi mata atau prosedur bedah.</li></ul><h3 style="color: #2c3e50; margin-bottom: 10px;">Cara Mencegah</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Melindungi mata dari cedera fisik dengan menggunakan kacamata pelindung saat bekerja atau berolahraga.</li><li style="margin-bottom: 10px;">Menghindari paparan bahan kimia atau iritan yang dapat merusak mata.</li><li style="margin-bottom: 10px;">Menjaga kebersihan mata, termasuk mencuci tangan sebelum menyentuh area mata.</li><li style="margin-bottom: 10px;">Mengelola kondisi kesehatan yang dapat memicu uveitis, seperti penyakit autoimun.</li><li style="margin-bottom: 10px;">Rutin memeriksakan mata ke dokter, terutama jika memiliki riwayat uveitis atau gangguan mata lainnya.</li></ul><p style="text-align: justify; margin-bottom: 15px;">Dengan penanganan yang tepat, uveitis dapat dikelola untuk mencegah komplikasi lebih lanjut. Pastikan untuk menjaga kesehatan mata dan segera mencari bantuan medis jika mengalami gejala yang mencurigakan.</p><h3 style="color: #2c3e50; margin-bottom: 10px;">Langkah Selanjutnya</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Segera jadwalkan konsultasi dengan dokter mata untuk pemeriksaan menyeluruh.</li><li style="margin-bottom: 10px;">Pastikan untuk menyampaikan semua gejala yang Anda alami secara detail kepada dokter.</li><li style="margin-bottom: 10px;">Bawa catatan medis atau riwayat kesehatan sebelumnya yang relevan, jika ada.</li><li style="margin-bottom: 10px;">Ikuti panduan dokter untuk langkah diagnostik atau pengobatan selanjutnya, seperti tes lanjutan atau penggunaan alat medis tertentu.</li></ul></div>""".trimIndent()
                else -> """<div style="font-family: Arial, sans-serif; line-height: 1.6; margin: 20px; padding: 20px; background: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);"><h2 style="color: #2c3e50; margin-bottom: 10px;">Kondisi Mata Tidak Diketahui</h2><p style="text-align: justify; margin-bottom: 15px;"><b>Informasi:</b> Mohon maaf, kami tidak dapat menentukan kondisi mata Anda berdasarkan data yang tersedia saat ini. Mungkin ada berbagai faktor yang memengaruhi hasil, termasuk kurangnya informasi atau gejala yang tidak spesifik.</p><h3 style="color: #2c3e50; margin-bottom: 10px;">Langkah Selanjutnya</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Segera jadwalkan konsultasi dengan dokter mata untuk pemeriksaan menyeluruh.</li><li style="margin-bottom: 10px;">Pastikan untuk menyampaikan semua gejala yang Anda alami secara detail kepada dokter.</li><li style="margin-bottom: 10px;">Bawa catatan medis atau riwayat kesehatan sebelumnya yang relevan, jika ada.</li><li style="margin-bottom: 10px;">Ikuti panduan dokter untuk langkah diagnostik atau pengobatan selanjutnya, seperti tes lanjutan atau penggunaan alat medis tertentu.</li></ul><h3 style="color: #2c3e50; margin-bottom: 10px;">Tips Umum untuk Menjaga Kesehatan Mata</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Konsumsi makanan yang kaya akan nutrisi untuk mata seperti wortel, bayam, dan ikan yang tinggi omega-3.</li><li style="margin-bottom: 10px;">Hindari kebiasaan buruk seperti menyentuh mata dengan tangan kotor.</li><li style="margin-bottom: 10px;">Jaga kebiasaan membaca dengan pencahayaan yang memadai untuk mengurangi tekanan pada mata.</li><li style="margin-bottom: 10px;">Lakukan pemeriksaan mata secara rutin, minimal setahun sekali, terutama jika Anda memiliki riwayat gangguan mata dalam keluarga.</li><li style="margin-bottom: 10px;">Gunakan pelindung mata saat bekerja di lingkungan yang berisiko seperti laboratorium atau area konstruksi.</li></ul><p style="text-align: justify; margin-bottom: 15px;">Kesehatan mata adalah aset penting untuk kehidupan sehari-hari. Jangan ragu untuk mendapatkan bantuan medis dari profesional jika ada masalah atau gejala yang tidak biasa. Langkah ini adalah bagian penting dalam menjaga penglihatan yang optimal.</p></div>""".trimIndent()
            }

            binding.tvKeterangan.text = HtmlCompat.fromHtml(htmlContent, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
//        bottomNavigationView.selectedItemId = -1

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navigateToMainActivity(R.id.navigation_home)
                    false
                }

                R.id.navigation_save -> {
                    navigateToMainActivity(R.id.navigation_save)
                    false
                }
                else -> false
            }
        }
        binding.saveButton.setOnClickListener {
            navigateToMainActivity(R.id.navigation_history)
        }

        binding.navView.menu.setGroupCheckable(0, false, false)
        binding.navView.menu.findItem(R.id.navigation_home).isChecked = false
        binding.navView.menu.findItem(R.id.navigation_save).isChecked = false

        binding.fabScan.setOnClickListener {
            val navViewScan = findViewById<BottomNavigationView>(R.id.nav_view)

            navViewScan.menu.setGroupCheckable(0, false, false)
            navViewScan.menu.findItem(R.id.navigation_home).isChecked = false
            navViewScan.menu.findItem(R.id.navigation_save).isChecked = false
            navigateToMainActivity(R.id.navigation_scan)
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

    @Deprecated(
        "This method has been deprecated in favor of using the {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}." +
                " The OnBackPressedDispatcher controls how back button events are dispatched to one or more {@link OnBackPressedCallback} objects."
    )
    override fun onBackPressed() {
        navigateToMainActivity(R.id.navigation_home)
        super.onBackPressed()
    }
}
