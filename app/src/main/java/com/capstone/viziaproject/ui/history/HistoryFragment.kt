package com.capstone.viziaproject.ui.history

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.viziaproject.data.pref.UserPreference
import com.capstone.viziaproject.data.pref.dataStore
import com.capstone.viziaproject.data.response.DataHistoryDetail
import com.capstone.viziaproject.databinding.FragmentHistoryBinding
import com.capstone.viziaproject.helper.ViewModelFactory
import com.capstone.viziaproject.ui.IntroActivity
import com.capstone.viziaproject.ui.scan.ScanViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {
    private val viewModel by viewModels<ScanViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HistoryAdapter
    private var isToastShown = false

    private lateinit var userPreference: UserPreference

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        userPreference = UserPreference.getInstance(requireContext().dataStore)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val user = userPreference.getSession().first()
            if (user.token.isNotEmpty() && user.isLogin) {
                adapter = HistoryAdapter(userPreference)
                viewModel.getHistoryUser(user.userId)
                setupRecyclerView()
                setupObservers()
                binding.pgError.visibility = View.GONE
                binding.contentGroup.visibility = View.VISIBLE
            } else {
                startActivity(Intent(requireContext(), IntroActivity::class.java))
                requireActivity().finish()
            }
        }

        if (!isInternetAvailable()) {
            binding.pgError.visibility = View.VISIBLE
            binding.contentGroup.visibility = View.GONE
        }
    }

    private fun setupObservers() {
        viewModel.getHistory.observe(viewLifecycleOwner) { histories ->
            if (!histories.isNullOrEmpty()) {
                adapter.submitList(histories)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistory.adapter = adapter
        adapter.setOnItemClickCallback(object : HistoryAdapter.OnItemClickCallback {
            override fun onItemClicked(detail: DataHistoryDetail) {
                val intent = Intent(requireContext(), DetailHistoryActivity::class.java)
                intent.putExtra(DetailHistoryActivity.EXTRA_HISTORY_DETAIL, detail)
                intent.putExtra("EVENT_ID", detail.id)
                startActivity(intent)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onResume() {
        super.onResume()
        context?.registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        context?.unregisterReceiver(connectivityReceiver)
    }

    private val connectivityReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onReceive(context: Context?, intent: Intent?) {
            if (!isInternetAvailable()) {
                binding.pgError.visibility = View.VISIBLE
                binding.contentGroup.visibility = View.GONE
                showError("No internet connection")
            } else {
                binding.pgError.visibility = View.GONE
                binding.contentGroup.visibility = View.VISIBLE
            }
        }
    }

    private fun showError(message: String) {
        if (!isToastShown) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            isToastShown = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
