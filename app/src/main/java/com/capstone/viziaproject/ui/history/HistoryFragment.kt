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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.viziaproject.data.response.DataItem
import com.capstone.viziaproject.data.response.DataItemHistory
import com.capstone.viziaproject.databinding.FragmentHistoryBinding
import com.capstone.viziaproject.helper.ViewModelFactory
import com.capstone.viziaproject.ui.IntroActivity
import com.capstone.viziaproject.ui.detailNews.DetailNewsActivity
import com.capstone.viziaproject.ui.home.HomeAdapter
import com.capstone.viziaproject.ui.scan.DiagnosisActivity
import com.capstone.viziaproject.ui.scan.ScanViewModel

class HistoryFragment : Fragment() {
    private val viewModel by viewModels<ScanViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private val adapter = HistoryAdapter()
    private var isToastShown = false

    @RequiresApi(Build.VERSION_CODES.M)
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("cekcek", "onCreateView called")
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            Log.d("cekcek", "User session: token=${user.token}, isLogin=${user.isLogin}")
            if (user.token.isNotEmpty() && user.isLogin) {
                Log.d("cekcek", "Formatted token: Bearer ${user.token}")
                viewModel.getHistoryUser(user.userId)
            } else {
                startActivity(Intent(requireContext(), IntroActivity::class.java))
                requireActivity().finish()
            }
        }

        if (isInternetAvailable()) {
            setupRecyclerView()
            setupObservers()
            binding.pgError.visibility = View.GONE
            binding.contentGroup.visibility = View.VISIBLE
        } else {
            binding.pgError.visibility = View.VISIBLE
            binding.contentGroup.visibility = View.GONE
        }
    }

    private fun setupObservers() {
        viewModel.getHistory.observe(viewLifecycleOwner) { stories ->
            if (!stories.isNullOrEmpty()) {
                adapter.submitList(stories)
            }
        }

//        viewModel.getSession().observe(viewLifecycleOwner) { user ->
//            user?.let {
//                viewModel.getHistoryUser(it.userId)
//            }
//        }

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
            override fun onItemClicked(data: DataItemHistory) {
                val intent = Intent(requireContext(), DiagnosisActivity::class.java)
//                intent.putExtra(DiagnosisActivity.USER_ID, data.id)
                startActivity(intent)
            }
        })
    }


    @RequiresApi(Build.VERSION_CODES.M)
    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()
        context?.registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        if (!isInternetAvailable()) {
            binding.pgError.visibility = View.VISIBLE
            binding.contentGroup.visibility = View.GONE
        }
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
                binding.rvHistory.visibility = View.GONE
                binding.tvEvent.visibility = View.GONE
                showError("No internet connection")
            } else {
                binding.pgError.visibility = View.GONE
                binding.contentGroup.visibility = View.VISIBLE

                binding.rvHistory.visibility = View.VISIBLE
                binding.tvEvent.visibility = View.VISIBLE

                viewModel.getSession().observe(this@HistoryFragment) { user ->
                    user?.let {
                        viewModel.getHistoryUser(it.userId)
                    }
                }
            }
        }
    }

    private fun showError(message: String) {
        if (!isToastShown) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            isToastShown = true
        }
        binding.contentGroup.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
        binding.pgError.visibility = View.VISIBLE
    }

}