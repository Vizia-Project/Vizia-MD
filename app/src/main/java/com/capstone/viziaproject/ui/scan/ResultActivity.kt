package com.capstone.viziaproject.ui.scan

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.capstone.viziaproject.R
import com.capstone.viziaproject.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getStringExtra("imageUri")?.let { Uri.parse(it) }
        val label = intent.getStringExtra("label")
        Log.d("cekcek", "Received Label: $label")
        val confidence = intent.getFloatExtra("confidence", 0.0f)

        val imageView: ImageView = findViewById(R.id.result_image)
        val labelView: TextView = findViewById(R.id.result_label)
        val confidenceView: TextView = findViewById(R.id.result_confidence)

        imageView.setImageURI(imageUri)
        val condition = when (label) {
            "Label 0" -> "Normal"
            "Label 1" -> "Stye"
            "Label 2" -> "Uveitis"
            else -> "Unknown"
        }

        labelView.text = "Result: $condition Eye"
        confidenceView.text = "Confidence: ${"%.2f".format(confidence * 100)}%"
    }
}