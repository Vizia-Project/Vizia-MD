package com.capstone.viziaproject.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.capstone.viziaproject.ml.Model
import com.capstone.viziaproject.ml.Vegs
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ImageClassifierHelper(
    private val context: Context,
    private val classifierListener: ClassifierListener?,
//    private val modelName: String = "vegs.tflite"
) {

    interface ClassifierListener {
        fun onResults(results: List<Pair<String, Float>>?,
                      inferenceTime: Long)
        fun onError(error: String)
    }

    private val imageClassifier: Model by lazy {
        Model.newInstance(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun classifyStaticImage(imageUri: Uri) {
        try {
            Log.d("cekcekimage", "Starting image classification for URI: $imageUri")

            // Decode and preprocess the image
            val bitmap = decodeBitmapFromUri(imageUri)?.let { Bitmap.createScaledBitmap(it, 128, 128, true) }
            if (bitmap == null) {
                classifierListener?.onError("Failed to decode the image.")
                return
            }

            Log.d("cekcekimage", "Decoded and resized bitmap to 128x128")

            // Convert the bitmap to ByteBuffer
            val byteBuffer = convertBitmapToByteBuffer(bitmap)
            Log.d("cekcekimage", "Bitmap converted to ByteBuffer")

            // Create TensorBuffer for input
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 128, 128, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(byteBuffer)

            Log.d("cekcekimage", "Input shape: ${inputFeature0.shape.contentToString()}")


            // Measure inference time
            val startTime = System.nanoTime()
            val outputs = imageClassifier.process(inputFeature0)
            val inferenceTime = System.nanoTime() - startTime

            Log.d("cekcekimage", "Inference completed in $inferenceTime nanoseconds")

            // Process the output
            val result = processModelOutput(outputs.outputFeature0AsTensorBuffer)
            classifierListener?.onResults(result, inferenceTime)

        } catch (e: Exception) {
            classifierListener?.onError("Error during image classification: ${e.message}")
            Log.e("cekcekimage", "Error classifying image: ${e.message}")
        }
    }

    private fun decodeBitmapFromUri(imageUri: Uri): Bitmap? {
        return try {
            Log.d("cekcekimage", "Decoding bitmap from URI: $imageUri")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val source = ImageDecoder.createSource(context.contentResolver, imageUri)
                ImageDecoder.decodeBitmap(source)
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            }
        } catch (e: Exception) {
            Log.e("cekcekimage", "Error decoding bitmap from URI: ${e.message}")
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        Log.d("cekcekimage", "Converting bitmap to ByteBuffer")

        // Convert HARDWARE bitmaps to ARGB_8888 for pixel access
        val mutableBitmap = if (bitmap.config == Bitmap.Config.HARDWARE) {
            bitmap.copy(Bitmap.Config.ARGB_8888, true)
        } else {
            bitmap
        }

        val byteBuffer = ByteBuffer.allocateDirect(4 * 128 * 128 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(128 * 128)
        mutableBitmap.getPixels(intValues, 0, 128, 0, 0, 128, 128)

        for (pixel in intValues) {
            val r = (pixel shr 16 and 0xFF) / 255.0f
            val g = (pixel shr 8 and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            byteBuffer.putFloat(r)
            byteBuffer.putFloat(g)
            byteBuffer.putFloat(b)
        }

        Log.d("cekcekimage", "Bitmap successfully converted to ByteBuffer")
        return byteBuffer
    }

    private fun processModelOutput(output: TensorBuffer): List<Pair<String, Float>> {
        val outputArray = output.floatArray
        val result = mutableListOf<Pair<String, Float>>()
        for (i in outputArray.indices) {
            result.add(Pair("Label $i", outputArray[i]))
        }
        Log.d("cekcekimage", "Processed model output: ${result.take(5)}")  // Log top 5 results
        return result.sortedByDescending { it.second }
    }

    fun close() {
        Log.d("cekcekimage", "Closing model and releasing resources")
        imageClassifier.close()
    }

    companion object {
        private const val TAG = "ImageClassifierHelper"
    }
}

