package com.capstone.viziaproject.ui.question

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.viziaproject.R
import com.capstone.viziaproject.databinding.ActivityQuest7Binding
import com.capstone.viziaproject.helper.ViewModelFactory
import com.capstone.viziaproject.ml.EyeDiagnosis
import com.capstone.viziaproject.ui.scan.DiagnosisActivity
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Collections
import java.text.SimpleDateFormat
import com.capstone.viziaproject.helper.Result
import com.capstone.viziaproject.helper.reduceFileImage
import com.capstone.viziaproject.helper.uriToFile
import com.capstone.viziaproject.ui.history.HistoryAdapter
import com.capstone.viziaproject.ui.scan.ScanViewModel
import com.loopj.android.http.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Date
import java.util.Locale

class Quest7Activity : AppCompatActivity() {
    private lateinit var binding: ActivityQuest7Binding

    private val position = 6
    private var answers: ArrayList<Int> = ArrayList()

    private lateinit var adapter: HistoryAdapter
    private val diagnosisViewModel: ScanViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuest7Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUriQuest = intent.getStringExtra("imageUri")?.let { Uri.parse(it) }
        val label = intent.getStringExtra("label")
        val confidence = intent.getFloatExtra("confidence", 0.0f)

        Log.d("cekcekquest7", "Received Label: $label")
        Log.d("cekcekquest7", "Received Confidence: $confidence")

        binding.imagePlaceholder.setImageURI(imageUriQuest)

        answers = intent.getIntegerArrayListExtra("answers") ?: ArrayList(Collections.nCopies(8, -1))
        val currentDateTime = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()).format(Date())

        when (answers[position]) {
            1 -> binding.radioYes.isChecked = true
            0 -> binding.radioNo.isChecked = true
        }


        diagnosisViewModel.getSession().observe(this) { userModel ->
            Log.d("cekcekquest7", "masukgetsession")
            if (userModel != null) {
                val userId = userModel.userId
                binding.nextButton.setOnClickListener {
                    Log.d("cekcekquest7", "klik nextButton")
                    diagnosisViewModel.setCurrentImageUri(imageUriQuest)
                    diagnosisViewModel.currentImageUri.value?.let { uri ->
                        lifecycleScope.launch {
                            binding.progressBar.visibility = View.VISIBLE

//                            val imageFile = withContext(Dispatchers.IO) {
//                                uriToFile(uri, this@Quest7Activity).reduceFileImage()
//                            }

                            val imageFile = uriToFile(uri, this@Quest7Activity)

                            Log.d("Image File", "showImage: ${imageFile.path}")

                            val answer =
                                if (binding.radioGroup.checkedRadioButtonId == R.id.radio_yes) 1 else 0
                            answers[position] = answer

                            if (label == "Label 0") {
                                answers[7] = 0
                            } else {
                                answers[7] = 1
                            }

                            val (diagnosisResult, confidenceResult) = getDiagnosisResult(answers)
                            var resultDisease = when (diagnosisResult) {
                                "Mata Normal" -> "Label 0"
                                "Mata Tidak Normal" -> label
                                else -> "Tidak Diketahui"
                            }
                            if (diagnosisResult === "Mata Tidak Normal" && resultDisease === "Label 0"){
                                resultDisease = "Tidak Diketahui"
                            }
                            val condition = when (resultDisease) {
                                "Label 0" -> "Mata Normal"
                                "Label 1" -> "Mata Hordeulum (Bintitan)"
                                "Label 2" -> "Mata Uveitis"
                                else -> "Tidak Diketahui"
                            }

                            val akurasiString = if (diagnosisResult == "Mata Normal") {
                                val adjustedAccuracy = 1.0f - confidenceResult
                                "%.2f".format(adjustedAccuracy * 100)
                            } else {
                                "%.2f".format(confidenceResult * 100)
                            }
                            val akurasi = akurasiString.replace(",", ".").toFloat()
                            val formattedAkurasi = "%.2f".format(akurasi)


                            val informasi = when (resultDisease) {
                                "Label 0" -> """<div style="font-family: Arial, sans-serif; line-height: 1.6; margin: 20px; padding: 0;"><h2 style=" color: #2c3e50; margin-bottom: 10px;">Mata Normal</h2><p style="text-align: justify; margin-bottom: 15px;">Mata Anda dalam kondisi normal. Menjaga kesehatan mata adalah langkah penting untuk memastikan penglihatan tetap optimal sepanjang hidup Anda. Berikut adalah beberapa tips yang dapat membantu Anda merawat mata agar tetap sehat.</p><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Konsumsi makanan kaya vitamin A, C, dan E seperti wortel, sayuran hijau, dan buah-buahan segar.</li><li style="margin-bottom: 10px;">Minum cukup air untuk menjaga kelembapan mata dan mencegah mata kering.</li><li style="margin-bottom: 10px;">Jaga kebersihan area sekitar mata, terutama jika Anda menggunakan riasan mata.</li><li style="margin-bottom: 10px;">Hindari menatap layar komputer atau ponsel terlalu lama tanpa istirahat. Terapkan aturan 20-20-20: setiap 20 menit, alihkan pandangan ke objek yang berjarak 20 kaki selama 20 detik.</li><li style="margin-bottom: 10px;">Gunakan kacamata pelindung saat berada di bawah sinar matahari yang terik untuk melindungi mata dari sinar UV.</li><li style="margin-bottom: 10px;">Rutin memeriksakan mata ke dokter untuk memastikan kondisi mata tetap sehat.</li></ul></div>""".trimIndent()
                                "Label 1" -> """<div style="font-family: Arial, sans-serif; line-height: 1.6; margin: 20px; padding: 20px; background: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);"><h2 style="color: #2c3e50; margin-bottom: 10px;">Mata Bintitan (Hordeulum/Stye)</h2><p style="text-align: justify; margin-bottom: 15px;"><b>Pengertian:</b> Stye atau bintitan adalah infeksi pada kelenjar di sekitar kelopak mata yang biasanya disebabkan oleh bakteri, seperti *Staphylococcus aureus*. Kondisi ini umum terjadi dan sering kali tidak berbahaya, tetapi dapat menyebabkan ketidaknyamanan.</p><h3 style="color: #2c3e50; margin-bottom: 10px;">Gejala</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Benjolan kecil, merah, dan menyakitkan di kelopak mata.</li><li style="margin-bottom: 10px;">Pembengkakan, nyeri, dan iritasi di sekitar area mata.</li><li style="margin-bottom: 10px;">Keluar nanah atau cairan dari benjolan tersebut.</li><li style="margin-bottom: 10px;">Rasa gatal atau sensasi mengganjal pada mata.</li></ul><h3 style="color: #2c3e50; margin-bottom: 10px;">Cara Mengatasi</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Kompres hangat selama 10-15 menit beberapa kali sehari untuk membantu mengurangi peradangan.</li><li style="margin-bottom: 10px;">Hindari memencet atau menyentuh benjolan untuk mencegah infeksi menyebar.</li><li style="margin-bottom: 10px;">Gunakan obat tetes mata atau salep antibiotik sesuai anjuran dokter jika diperlukan.</li><li style="margin-bottom: 10px;">Jika gejala tidak membaik dalam beberapa hari, segera konsultasikan dengan dokter spesialis mata.</li></ul><h3 style="color: #2c3e50; margin-bottom: 10px;">Cara Mencegah</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Cuci tangan secara rutin sebelum menyentuh area mata untuk menghindari bakteri masuk.</li><li style="margin-bottom: 10px;">Jangan menggunakan produk kosmetik mata yang sudah kadaluwarsa atau milik orang lain.</li><li style="margin-bottom: 10px;">Hindari berbagi handuk, bantal, atau alat rias dengan orang lain.</li><li style="margin-bottom: 10px;">Bersihkan riasan mata sebelum tidur untuk mencegah penumpukan kotoran di sekitar kelopak mata.</li></ul><p style="text-align: justify; margin-bottom: 15px;">Menjaga kebersihan dan mengadopsi kebiasaan sehat sangat penting untuk mencegah stye. Jika Anda mengalami gejala berulang, konsultasikan dengan dokter untuk evaluasi lebih lanjut.</p><h3 style="color: #2c3e50; margin-bottom: 10px;">Langkah Selanjutnya</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Segera jadwalkan konsultasi dengan dokter mata untuk pemeriksaan menyeluruh.</li><li style="margin-bottom: 10px;">Pastikan untuk menyampaikan semua gejala yang Anda alami secara detail kepada dokter.</li><li style="margin-bottom: 10px;">Bawa catatan medis atau riwayat kesehatan sebelumnya yang relevan, jika ada.</li><li style="margin-bottom: 10px;">Ikuti panduan dokter untuk langkah diagnostik atau pengobatan selanjutnya, seperti tes lanjutan atau penggunaan alat medis tertentu.</li></ul></div>""".trimIndent()
                                "Label 2" -> """<div style="font-family: Arial, sans-serif; line-height: 1.6; margin: 20px; padding: 20px; background: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);"><h2 style="color: #2c3e50; margin-bottom: 10px;">Uveitis Eye</h2><p style="text-align: justify; margin-bottom: 15px;"><b>Pengertian:</b> Uveitis adalah peradangan pada lapisan tengah mata, yang dikenal sebagai uvea. Uvea adalah bagian mata yang berfungsi penting dalam menyuplai darah ke retina. Kondisi ini dapat memengaruhi satu atau kedua mata, dengan tingkat keparahan yang bervariasi. Jika tidak segera diobati, uveitis dapat menyebabkan komplikasi serius seperti glaukoma, katarak, atau bahkan kehilangan penglihatan.</p><h3 style="color: #2c3e50; margin-bottom: 10px;">Gejala</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Kemerahan pada mata yang sering kali disertai peradangan.</li><li style="margin-bottom: 10px;">Penglihatan buram atau kabur yang dapat memburuk seiring waktu.</li><li style="margin-bottom: 10px;">Nyeri pada mata, terutama saat terkena cahaya terang (fotofobia).</li><li style="margin-bottom: 10px;">Munculnya floaters (bayangan kecil yang tampak mengapung dalam penglihatan).</li><li style="margin-bottom: 10px;">Penurunan penglihatan yang tiba-tiba pada beberapa kasus akut.</li></ul><h3 style="color: #2c3e50; margin-bottom: 10px;">Cara Mengatasi</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Segera konsultasi dengan dokter mata untuk diagnosis dan pengobatan yang tepat.</li><li style="margin-bottom: 10px;">Penggunaan obat tetes mata yang mengandung steroid atau antiinflamasi sesuai resep dokter.</li><li style="margin-bottom: 10px;">Jika disebabkan oleh infeksi, dokter mungkin akan meresepkan antibiotik atau antivirus.</li><li style="margin-bottom: 10px;">Mengelola stres dan menjaga pola hidup sehat untuk mempercepat pemulihan.</li><li style="margin-bottom: 10px;">Pada kasus yang lebih parah, mungkin diperlukan injeksi mata atau prosedur bedah.</li></ul><h3 style="color: #2c3e50; margin-bottom: 10px;">Cara Mencegah</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Melindungi mata dari cedera fisik dengan menggunakan kacamata pelindung saat bekerja atau berolahraga.</li><li style="margin-bottom: 10px;">Menghindari paparan bahan kimia atau iritan yang dapat merusak mata.</li><li style="margin-bottom: 10px;">Menjaga kebersihan mata, termasuk mencuci tangan sebelum menyentuh area mata.</li><li style="margin-bottom: 10px;">Mengelola kondisi kesehatan yang dapat memicu uveitis, seperti penyakit autoimun.</li><li style="margin-bottom: 10px;">Rutin memeriksakan mata ke dokter, terutama jika memiliki riwayat uveitis atau gangguan mata lainnya.</li></ul><p style="text-align: justify; margin-bottom: 15px;">Dengan penanganan yang tepat, uveitis dapat dikelola untuk mencegah komplikasi lebih lanjut. Pastikan untuk menjaga kesehatan mata dan segera mencari bantuan medis jika mengalami gejala yang mencurigakan.</p><h3 style="color: #2c3e50; margin-bottom: 10px;">Langkah Selanjutnya</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Segera jadwalkan konsultasi dengan dokter mata untuk pemeriksaan menyeluruh.</li><li style="margin-bottom: 10px;">Pastikan untuk menyampaikan semua gejala yang Anda alami secara detail kepada dokter.</li><li style="margin-bottom: 10px;">Bawa catatan medis atau riwayat kesehatan sebelumnya yang relevan, jika ada.</li><li style="margin-bottom: 10px;">Ikuti panduan dokter untuk langkah diagnostik atau pengobatan selanjutnya, seperti tes lanjutan atau penggunaan alat medis tertentu.</li></ul></div>""".trimIndent()
                                else -> """<div style="font-family: Arial, sans-serif; line-height: 1.6; margin: 20px; padding: 20px; background: #ffffff; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);"><h2 style="color: #2c3e50; margin-bottom: 10px;">Kondisi Mata Tidak Diketahui</h2><p style="text-align: justify; margin-bottom: 15px;"><b>Informasi:</b> Mohon maaf, kami tidak dapat menentukan kondisi mata Anda berdasarkan data yang tersedia saat ini. Mungkin ada berbagai faktor yang memengaruhi hasil, termasuk kurangnya informasi atau gejala yang tidak spesifik.</p><h3 style="color: #2c3e50; margin-bottom: 10px;">Langkah Selanjutnya</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Segera jadwalkan konsultasi dengan dokter mata untuk pemeriksaan menyeluruh.</li><li style="margin-bottom: 10px;">Pastikan untuk menyampaikan semua gejala yang Anda alami secara detail kepada dokter.</li><li style="margin-bottom: 10px;">Bawa catatan medis atau riwayat kesehatan sebelumnya yang relevan, jika ada.</li><li style="margin-bottom: 10px;">Ikuti panduan dokter untuk langkah diagnostik atau pengobatan selanjutnya, seperti tes lanjutan atau penggunaan alat medis tertentu.</li></ul><h3 style="color: #2c3e50; margin-bottom: 10px;">Tips Umum untuk Menjaga Kesehatan Mata</h3><ul style="text-align: justify; padding-left: 20px; list-style-type: square; margin-bottom: 20px;"><li style="margin-bottom: 10px;">Konsumsi makanan yang kaya akan nutrisi untuk mata seperti wortel, bayam, dan ikan yang tinggi omega-3.</li><li style="margin-bottom: 10px;">Hindari kebiasaan buruk seperti menyentuh mata dengan tangan kotor.</li><li style="margin-bottom: 10px;">Jaga kebiasaan membaca dengan pencahayaan yang memadai untuk mengurangi tekanan pada mata.</li><li style="margin-bottom: 10px;">Lakukan pemeriksaan mata secara rutin, minimal setahun sekali, terutama jika Anda memiliki riwayat gangguan mata dalam keluarga.</li><li style="margin-bottom: 10px;">Gunakan pelindung mata saat bekerja di lingkungan yang berisiko seperti laboratorium atau area konstruksi.</li></ul><p style="text-align: justify; margin-bottom: 15px;">Kesehatan mata adalah aset penting untuk kehidupan sehari-hari. Jangan ragu untuk mendapatkan bantuan medis dari profesional jika ada masalah atau gejala yang tidak biasa. Langkah ini adalah bagian penting dalam menjaga penglihatan yang optimal.</p></div>""".trimIndent()
                            }
                            val informasii="tes"
                            Log.d("cekcekquest7","$imageUriQuest")
                            Log.d("cekcekquest7", """
                                Preparing to send data to diagnosisViewModel.store:
                                - Image File Path: ${imageFile.path}
                                - User ID: $userId
                                - Current DateTime: $currentDateTime
                                - Answers: $answers
                                - Condition: $condition
                                - Diagnosis Result: $diagnosisResult
                                - Accuracy: $akurasi
                                - Informasi: $informasi
                            """.trimIndent())


                            diagnosisViewModel.store(
                                userId,
                                currentDateTime,
                                imageFile,
                                answers,
                                diagnosisResult,
                                condition,
                                akurasi.toDouble(),
                                informasi
                            ).observe(this@Quest7Activity) { result ->
                                when (result) {
                                    is Result.Loading -> {
                                        binding.progressBar.visibility = View.VISIBLE
                                        Log.d("cekcekquest7", "loadingg")
                                    }
                                    is Result.Success -> {
                                        binding.progressBar.visibility = View.GONE
                                        if (result.data.status != "success") {
                                            Log.d("cekcekquest7","Terjadi kesalahan sukses: ${result.data.message}")
                                            Log.e("cekcekquest7", """
                                                Error Details:
                                                - Image Path: ${imageFile.path}
                                                - User ID: $userId
                                                - Current DateTime: $currentDateTime
                                                - Answers: ${answers.joinToString(", ")}
                                                - Condition: $condition
                                                - Diagnosis Result: $diagnosisResult
                                                - Accuracy: $akurasi
                                                - Informasi: $informasi
                                            """.trimIndent())
                                            Toast.makeText(this@Quest7Activity, result.data.message, Toast.LENGTH_SHORT).show()
                                        } else {
                                            val intent = Intent(this@Quest7Activity, DiagnosisActivity::class.java).apply {
                                                putIntegerArrayListExtra("answers", answers)
                                                putExtra("imageUriQuest", imageUriQuest.toString())
                                                putExtra("label", label)
                                                putExtra("confidence", confidence)
                                                putExtra("diagnosisResult", diagnosisResult)
                                                putExtra("accuracy", confidenceResult)
                                                putExtra("dateTime", currentDateTime)
                                            }
                                            Log.d("cekcekquest7","berhasillll")
                                            Toast.makeText(this@Quest7Activity, "Analisis berhasil!", Toast.LENGTH_SHORT).show()
                                            startActivity(intent)
                                        }
                                    }
                                    is Result.Error -> {
                                        binding.progressBar.visibility = View.GONE
                                        Log.d("cekcekquest7","Terjadi kesalahan: ${result.error}")
                                        Log.e("cekcekquest7", """
                                        Error Details Bawah:
                                        - Image Path: ${imageFile.path}
                                        - User ID: $userId
                                        - Current DateTime: $currentDateTime
                                        - Answers: ${answers.joinToString(", ")}
                                        - Condition: $condition
                                        - Diagnosis Result: $diagnosisResult
                                        - Accuracy: $akurasi
                                        - Informasi: $informasi
                                    """.trimIndent())
                                        Toast.makeText(this@Quest7Activity, "Terjadi kesalahan: ${result.error}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }



        binding.backButton.setOnClickListener {
            val answer = if (binding.radioGroup.checkedRadioButtonId == R.id.radio_yes) 1 else 0
            answers[position] = answer

            val resultIntent = Intent().apply {
                putIntegerArrayListExtra("answers", answers)
                putExtra("imageUriQuest", imageUriQuest.toString())
                putExtra("label", label)
                putExtra("confidence", confidence)
            }

            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun getDiagnosisResult(answers: ArrayList<Int>): Pair<String, Float> {
        val byteBuffer = ByteBuffer.allocateDirect(answers.size * 4) // Float is 4 bytes
        byteBuffer.order(ByteOrder.nativeOrder())

        Log.d("cekcekdiagnosis", "Filling ByteBuffer with answers: ${answers.joinToString(", ")}")
        for (answer in answers) {
            byteBuffer.putFloat(answer.toFloat())
        }

        Log.d("cekcekdiagnosis", "Loading TensorFlow Lite model")
        val model = EyeDiagnosis.newInstance(this)

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 8), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)
        Log.d("cekcekdiagnosis", "TensorBuffer loaded with byte data")

        Log.d("cekcekdiagnosis", "Running inference with the model")
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        Log.d("cekcekdiagnosis", "Closing model resources")
        model.close()

        val result = outputFeature0.floatArray
        Log.d("cekcekdiagnosis", "Model output: ${result.joinToString(", ")}")


        val predictedLabel = result.withIndex().maxByOrNull { it.value }?.index ?: -1
        val accuracy = result.maxOrNull() ?: 0f  // Get the confidence score for the prediction

        Log.d("DiagnosisResult", "Predicted label index: $predictedLabel")
        Log.d("DiagnosisResult", "Confidence: $accuracy")

        val diagnosis = if (accuracy > 0.5) {
            "Mata Tidak Normal"
        } else {
            "Mata Normal"
        }

        Log.d("cekcekdiagnosis", "Diagnosis result: $diagnosis")
        return diagnosis to accuracy
    }

}
