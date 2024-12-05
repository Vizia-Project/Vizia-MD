package com.capstone.viziaproject.ui.question

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.capstone.viziaproject.R
import com.capstone.viziaproject.databinding.ActivityQuest4Binding
import java.util.Collections

class Quest4Activity : AppCompatActivity() {
    private lateinit var binding: ActivityQuest4Binding

    private val position = 3
    private var answers: ArrayList<Int> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuest4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra("imageUri")?.let { Uri.parse(it) }
        val label = intent.getStringExtra("label")
        val confidence = intent.getFloatExtra("confidence", 0.0f)

        Log.d("cekcekquest4", "Received Label: $label")
        Log.d("cekcekquest4", "Received Confidence: $confidence")

        binding.imagePlaceholder.setImageURI(imageUri)

        answers = intent.getIntegerArrayListExtra("answers") ?: ArrayList(Collections.nCopies(8, -1))

        when (answers[position]) {
            1 -> binding.radioYes.isChecked = true
            0 -> binding.radioNo.isChecked = true
        }

        binding.nextButton.setOnClickListener {
            val answer = if (binding.radioGroup.checkedRadioButtonId == R.id.radio_yes) 1 else 0
            answers[position] = answer

            val intent = Intent(this, Quest5Activity::class.java).apply {
                putIntegerArrayListExtra("answers", answers)
                putExtra("imageUri", imageUri.toString())
                putExtra("label", label)
                putExtra("confidence", confidence)
            }
            startActivityForResult(intent, REQUEST_CODE_QUEST5)
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
            finish() // Finish Quest4Activity and return to Quest3Activity
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_QUEST5 && resultCode == RESULT_OK) {
            val updatedAnswers = data?.getIntegerArrayListExtra("answers")
            if (updatedAnswers != null) {
                answers = updatedAnswers
            }
        }
    }

    companion object {
        const val REQUEST_CODE_QUEST5 = 4
    }
}
