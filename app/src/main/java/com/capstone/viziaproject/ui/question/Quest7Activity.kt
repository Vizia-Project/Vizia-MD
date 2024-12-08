package com.capstone.viziaproject.ui.question

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.capstone.viziaproject.R
import com.capstone.viziaproject.databinding.ActivityQuest7Binding
import com.capstone.viziaproject.helper.ViewModelFactory
import com.capstone.viziaproject.ml.EyeDiagnosis
import com.capstone.viziaproject.ui.scan.DiagnosisActivity
import com.capstone.viziaproject.ui.scan.DiagnosisViewModel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Collections
import java.text.SimpleDateFormat
import com.capstone.viziaproject.helper.Result
import com.capstone.viziaproject.helper.reduceFileImage
import com.capstone.viziaproject.helper.uriToFile
import com.capstone.viziaproject.ui.scan.ScanViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.Locale

class Quest7Activity : AppCompatActivity() {
    private lateinit var binding: ActivityQuest7Binding

    private val position = 6
    private var answers: ArrayList<Int> = ArrayList()

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

                            val imageFile = withContext(Dispatchers.IO) {
                                uriToFile(uri, this@Quest7Activity).reduceFileImage()
                            }

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
                            val resultDisease = when (diagnosisResult) {
                                "Negative" -> "Label 0"
                                "Positive" -> label
                                else -> "Unknown"
                            }
                            val condition = when (resultDisease) {
                                "Label 0" -> "Normal Eye"
                                "Label 1" -> "Stye / Hordeulum Eye"
                                "Label 2" -> "Uveitis Eye"
                                else -> "Unknown"
                            }
                            val akurasiString = if (diagnosisResult == "Negative") {
                                val adjustedAccuracy = 1.0f - confidenceResult
                                "%.2f".format(adjustedAccuracy * 100)
                            } else {
                                "%.2f".format(confidenceResult * 100)
                            }
                            val akurasi = akurasiString.replace(",", ".").toFloat()
                            val formattedAkurasi = "%.2f".format(akurasi)

                            val informasi = when (resultDisease) {
                                "Label 0" -> "<h2>Mata Normal</h2><p>Mata Anda dalam kondisi normal. Berikut adalah beberapa tips untuk menjaga kesehatan mata Anda:</p><ul><li>Konsumsi makanan kaya vitamin A, C, dan E seperti wortel dan sayuran hijau.</li><li>Jaga kebersihan area sekitar mata.</li><li>Hindari menatap layar komputer atau ponsel terlalu lama tanpa istirahat.</li><li>Gunakan kacamata pelindung saat berada di bawah sinar matahari yang terik.</li></ul>"
                                "Label 1" -> "<h2>Stye Eye</h2><p><b>Pengertian:</b> Stye atau bintitan adalah infeksi pada kelenjar di sekitar kelopak mata yang biasanya disebabkan oleh bakteri.</p><p><b>Gejala:</b></p><ul><li>Benjolan kecil dan merah di kelopak mata.</li><li>Pembengkakan, nyeri, dan iritasi di sekitar mata.</li><li>Keluar nanah dari benjolan.</li></ul><p><b>Cara Mengatasi:</b></p><ul><li>Kompres hangat selama 10-15 menit beberapa kali sehari.</li><li>Hindari memencet atau menyentuh benjolan.</li><li>Gunakan obat tetes mata antibiotik sesuai anjuran dokter.</li></ul><p><b>Cara Mencegah:</b></p><ul><li>Selalu cuci tangan sebelum menyentuh mata.</li><li>Jangan menggunakan produk kosmetik mata yang sudah kadaluwarsa.</li><li>Hindari berbagi handuk atau alat rias mata dengan orang lain.</li></ul>"
                                "Label 2" -> "<h2>Uveitis Eye</h2><p><b>Pengertian:</b> Uveitis adalah peradangan pada lapisan tengah mata (uvea), yang dapat menyebabkan gangguan penglihatan jika tidak segera diatasi.</p><p><b>Gejala:</b></p><ul><li>Kemerahan pada mata.</li><li>Penglihatan buram atau kabur.</li><li>Nyeri pada mata, terutama saat terkena cahaya terang.</li></ul><p><b>Cara Mengatasi:</b></p><ul><li>Konsultasi ke dokter mata untuk mendapatkan obat tetes mata steroid atau antiinflamasi.</li><li>Hindari stres yang dapat memperburuk peradangan.</li></ul><p><b>Cara Mencegah:</b></p><ul><li>Lindungi mata dari cedera atau infeksi.</li><li>Hindari paparan bahan kimia atau iritan.</li><li>Jaga kebersihan mata secara rutin.</li></ul>"
                                else -> "<h2>Tidak Diketahui</h2><p>Mohon maaf, kami tidak dapat menentukan kondisi mata Anda.</p><p>Silakan konsultasikan lebih lanjut dengan dokter mata untuk diagnosis dan pengobatan yang tepat.</p>"
                            }

                            Log.d(
                                "cekcekquest7",
                                "User ID: $userId, Akurasi: $akurasi, $answers, $condition, $currentDateTime, $imageFile"
                            )
                            diagnosisViewModel.store(
                                imageFile,
                                userId,
                                currentDateTime,
                                answers,
                                condition,
                                diagnosisResult,
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
                                        Toast.makeText(this@Quest7Activity, "Diagnosis berhasil disimpan!", Toast.LENGTH_SHORT).show()
                                        startActivity(intent)
                                    }
                                    is Result.Error -> {
                                        binding.progressBar.visibility = View.GONE
                                        Log.d("cekcekquest7","Terjadi kesalahan: ${result.error}")
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
            "Positive"
        } else {
            "Negative"
        }

        Log.d("cekcekdiagnosis", "Diagnosis result: $diagnosis")
        return diagnosis to accuracy
    }

}
