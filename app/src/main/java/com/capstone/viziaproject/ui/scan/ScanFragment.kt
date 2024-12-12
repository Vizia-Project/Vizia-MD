package com.capstone.viziaproject.ui.scan
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.capstone.viziaproject.R
import com.capstone.viziaproject.databinding.FragmentScanBinding
import com.capstone.viziaproject.helper.ImageClassifierHelper
import com.capstone.viziaproject.helper.ViewModelFactory
import com.capstone.viziaproject.helper.getImageUri
import com.capstone.viziaproject.ui.main.MainActivity
import com.capstone.viziaproject.ui.question.Quest1Activity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ScanFragment : Fragment(), ImageClassifierHelper.ClassifierListener {

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }

    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private var _binding: FragmentScanBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private val viewModel by viewModels<ScanViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            binding.imagePlaceholder.setImageURI(it)
            viewModel.setCurrentImageUri(it)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            binding.imagePlaceholder.setImageURI(viewModel.currentImageUri.value)
        } else {
            viewModel.setCurrentImageUri(null)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            val message = if (isGranted) "Permission granted" else "Permission denied"
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            requireContext(),
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    private fun startCamera() {
        viewModel.setCurrentImageUri(getImageUri(requireContext()))
        launcherIntentCamera.launch(viewModel.currentImageUri.value)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun analyzeImage() {
        val currentImageUri = viewModel.currentImageUri.value
        Log.d("cekcekscan", "current image uri $currentImageUri")
        if (currentImageUri != null) {
            binding.progressBar.visibility = View.VISIBLE
            imageClassifierHelper.classifyStaticImage(currentImageUri)
        } else {
            Toast.makeText(requireContext(), "Please select an image first", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScanBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.galeriButton.setOnClickListener { startGallery() }
        binding.kameraButton.setOnClickListener { startCamera() }
        binding.analyzeButton.setOnClickListener { analyzeImage() }

        imageClassifierHelper = ImageClassifierHelper(
            context = requireContext(),
            classifierListener = this
        )

        (activity as? MainActivity)?.updateBottomNavigationStateForScanFragment()


        if (isInternetAvailable()) {
            internet()
        } else {
            noInternet()
        }
    }

    override fun onResults(results: List<Pair<String, Float>>?, inferenceTime: Long) {
        binding.progressBar.visibility = View.GONE
        if (!results.isNullOrEmpty()) {
            val topResult = results.maxByOrNull { it.second }
            moveToResult(topResult?.first, topResult?.second)
//            showToast("Prediction: ${topResult?.first} with confidence ${topResult?.second}")
        } else {
            showToast("No results found.")
        }
    }

    override fun onError(error: String) {
        showToast(error)
    }

    private fun internet(){
        binding.pgError.visibility = View.GONE
        binding.title.visibility = View.VISIBLE
        binding.subtitle.visibility = View.VISIBLE
        binding.instructionText.visibility = View.VISIBLE
        binding.layoutImage.visibility = View.VISIBLE
        binding.galeriButton.visibility = View.VISIBLE
        binding.kameraButton.visibility = View.VISIBLE
        binding.analyzeButton.visibility = View.VISIBLE
    }
    private fun noInternet(){
        binding.pgError.visibility = View.VISIBLE
        binding.title.visibility = View.GONE
        binding.subtitle.visibility = View.GONE
        binding.instructionText.visibility = View.GONE
        binding.layoutImage.visibility = View.GONE
        binding.galeriButton.visibility = View.GONE
        binding.kameraButton.visibility = View.GONE
        binding.analyzeButton.visibility = View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun moveToResult(label: String?, confidence: Float?) {
        Log.d("cekcekimage", "Label: $label, Confidence: $confidence")
        val intent = Intent(requireContext(), Quest1Activity::class.java).apply {
            putExtra("imageUri", viewModel.currentImageUri.value.toString())
            putExtra("label", label)
            putExtra("confidence", confidence)
        }
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        val navView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        navView?.menu?.setGroupCheckable(0, true, true)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        if (isInternetAvailable()) {
            internet()
        } else {
            noInternet()
        }
        val navView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        navView?.menu?.setGroupCheckable(0, true, true)
        navView?.menu?.findItem(R.id.navigation_scan)?.isChecked = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        imageClassifierHelper.close()
        _binding = null
    }
}
