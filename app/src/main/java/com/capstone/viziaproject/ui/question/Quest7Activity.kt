package com.capstone.viziaproject.ui.question

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.capstone.viziaproject.R
import com.capstone.viziaproject.databinding.ActivityQuest7Binding
import com.capstone.viziaproject.ml.EyeDiagnosis
import com.capstone.viziaproject.ui.scan.DiagnosisActivity
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Collections
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("NAME_SHADOWING")
class Quest7Activity : AppCompatActivity() {
    private lateinit var binding: ActivityQuest7Binding

    private val position = 6
    private var answers: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuest7Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra("imageUri")?.let { Uri.parse(it) }
        val label = intent.getStringExtra("label")
        val confidence = intent.getFloatExtra("confidence", 0.0f)

        Log.d("cekcekquest7", "Received Label: $label")
        Log.d("cekcekquest7", "Received Confidence: $confidence")

        binding.imagePlaceholder.setImageURI(imageUri)

        answers = intent.getIntegerArrayListExtra("answers") ?: ArrayList(Collections.nCopies(8, -1))
        val currentDateTime = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault()).format(Date())

        when (answers[position]) {
            1 -> binding.radioYes.isChecked = true
            0 -> binding.radioNo.isChecked = true
        }

        binding.nextButton.setOnClickListener {
            val answer = if (binding.radioGroup.checkedRadioButtonId == R.id.radio_yes) 1 else 0
            answers[position] = answer

            if (label == "Label 0") {
                answers[7] = 0
            } else {
                answers[7] = 1
            }

            Log.d("cekcekquest7", "Updated answers: ${answers.joinToString(", ")}")

            val (diagnosisResult, confidenceResult) = getDiagnosisResult(answers)

            val intent = Intent(this, DiagnosisActivity::class.java).apply {
                putIntegerArrayListExtra("answers", answers)
                putExtra("imageUri", imageUri.toString())
                putExtra("label", label)
                putExtra("confidence", confidence)
                putExtra("diagnosisResult", diagnosisResult)
                putExtra("accuracy", confidenceResult)
                putExtra("dateTime", currentDateTime)
            }
            startActivity(intent)
        }


        binding.backButton.setOnClickListener {
            val answer = if (binding.radioGroup.checkedRadioButtonId == R.id.radio_yes) 1 else 0
            answers[position] = answer

            val resultIntent = Intent().apply {
                putIntegerArrayListExtra("answers", answers)
                putExtra("imageUri", imageUri.toString())
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
