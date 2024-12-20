package com.capstone.viziaproject.ui.home

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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.viziaproject.R
import com.capstone.viziaproject.data.response.DataItem
import com.capstone.viziaproject.databinding.FragmentHomeBinding
import com.capstone.viziaproject.helper.ViewModelFactory
import com.capstone.viziaproject.ui.IntroActivity
import com.capstone.viziaproject.ui.detailNews.DetailNewsActivity
import com.capstone.viziaproject.ui.login.LoginActivity
import com.capstone.viziaproject.ui.news.NewsFragment
import com.capstone.viziaproject.ui.profil.ProfilFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeFragment : Fragment(), View.OnClickListener {
    private val viewModel by viewModels<HomeViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val adapter = HomeAdapter()
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getSession().observe(viewLifecycleOwner) { user ->
            Log.d("cekcek", "User session: token=${user.token}, isLogin=${user.isLogin}")
            if (user.token.isNotEmpty() && user.isLogin) {
                Log.d("cekcek", "Formatted token: Bearer ${user.token}")
                fragmentProfil(user.userId)
                viewModel.getArticle()
            } else {
                startActivity(Intent(requireContext(), IntroActivity::class.java))
                requireActivity().finish()
            }
        }
        if (isInternetAvailable()) {
            setupRecyclerView()
            setupObservers()
            setupActions()
            binding.pgError.visibility = View.GONE
        } else {
            binding.pgError.visibility = View.VISIBLE
        }
    }

    private fun fragmentProfil(userId: Int) {
        binding.buttonProfil.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("USER_ID", userId)
            }
            val navController = findNavController()
            val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)

            // Pass the bundle with userId to the ProfilFragment
            navController.navigate(R.id.navigation_profil, bundle)
        }
    }


    private fun setupRecyclerView() {
        binding.rvArtikel.layoutManager = LinearLayoutManager(requireContext())
        binding.rvArtikel.adapter = adapter
        adapter.setOnItemClickCallback(object : HomeAdapter.OnItemClickCallback {
            override fun onItemClicked(data: DataItem) {
                val intent = Intent(requireContext(), DetailNewsActivity::class.java)
                intent.putExtra(DetailNewsActivity.STORY_URL, data.sourceUrl)
                startActivity(intent)
            }
        })
    }

    private fun setupObservers() {
        viewModel.listNews.observe(viewLifecycleOwner) { stories ->
            if (!stories.isNullOrEmpty()) {
                adapter.submitList(stories)
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

    private fun setupActions() {
        binding.buttonScan.setOnClickListener(this)
        binding.buttonSaved.setOnClickListener(this)
        binding.buttonNews.setOnClickListener(this)
        binding.buttonHistory.setOnClickListener(this)
    }
    @RequiresApi(Build.VERSION_CODES.M)
    @Suppress("DEPRECATION")
    override fun onResume() {
        super.onResume()
        context?.registerReceiver(connectivityReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        if (!isInternetAvailable()) {
            binding.pgError.visibility = View.VISIBLE
            binding.layoutQuick.visibility = View.GONE
            binding.quickAccess.visibility = View.GONE
            binding.imageView.visibility = View.INVISIBLE
            binding.newsArtikel.visibility = View.GONE
            binding.rvArtikel.visibility = View.GONE
        }else {
            binding.pgError.visibility = View.GONE
            binding.rvArtikel.visibility = View.VISIBLE
            binding.quickAccess.visibility = View.VISIBLE
            binding.newsArtikel.visibility = View.VISIBLE
            binding.layoutQuick.visibility = View.VISIBLE
            binding.imageView.visibility = View.VISIBLE
            viewModel.fetchEvents()
            setupRecyclerView()
            setupObservers()
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
                binding.rvArtikel.visibility = View.GONE
                binding.quickAccess.visibility = View.GONE
                binding.newsArtikel.visibility = View.GONE
                binding.layoutQuick.visibility = View.GONE
                binding.imageView.visibility = View.INVISIBLE
                showError("No internet connection")
            } else {
                binding.pgError.visibility = View.GONE
                binding.rvArtikel.visibility = View.VISIBLE
                binding.quickAccess.visibility = View.VISIBLE
                binding.newsArtikel.visibility = View.VISIBLE
                binding.layoutQuick.visibility = View.VISIBLE
                binding.imageView.visibility = View.VISIBLE
                viewModel.fetchEvents()
                setupRecyclerView()
                setupObservers()
            }
        }
    }

    private fun showError(message: String) {
        if (!isToastShown) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            isToastShown = true
        }
        binding.rvArtikel.visibility = View.GONE
        binding.quickAccess.visibility = View.GONE
        binding.newsArtikel.visibility = View.GONE
        binding.layoutQuick.visibility = View.GONE
        binding.imageView.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.GONE
        binding.pgError.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        val navController = findNavController()
        val bottomNavView = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)
        when (v?.id) {
            R.id.buttonNews -> {
                navController.navigate(R.id.navigation_news)
            }
            R.id.buttonSaved -> {
                navController.navigate(R.id.navigation_save)
            }
            R.id.buttonScan -> {
                navController.navigate(R.id.navigation_scan)
                bottomNavView.menu.setGroupCheckable(0, false, false)
                bottomNavView.menu.findItem(R.id.navigation_home).isChecked = false
                bottomNavView.menu.findItem(R.id.navigation_save).isChecked = false
            }
            R.id.buttonHistory -> {
                navController.navigate(R.id.navigation_history)
            }
        }
    }
}
