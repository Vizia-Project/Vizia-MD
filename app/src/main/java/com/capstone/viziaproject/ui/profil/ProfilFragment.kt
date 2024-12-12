package com.capstone.viziaproject.ui.profil

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.capstone.viziaproject.R
import com.capstone.viziaproject.data.response.UserResponse
import com.capstone.viziaproject.databinding.FragmentProfilBinding
import com.capstone.viziaproject.helper.ViewModelFactory
import com.capstone.viziaproject.helper.Result
import com.capstone.viziaproject.ui.home.HomeViewModel
import com.capstone.viziaproject.ui.login.LoginActivity

class ProfilFragment : Fragment() {

    private lateinit var binding: FragmentProfilBinding
    private val profileViewModel by viewModels<ProfilViewModel> {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilBinding.inflate(inflater, container, false)

        val userId = arguments?.getInt("USER_ID") ?: 0
        if (userId != 0) {
            observeUserProfile(userId)
        }

        return binding.root
    }

    private fun observeUserProfile(userId: Int) {
        profileViewModel.getUserProfile(userId).observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    showLoading(true)
                }
                is Result.Success -> {
                    showLoading(false)
                    val userResponse = result.data
                    populateProfile(userResponse)
                }
                is Result.Error -> {
                    showLoading(false)  // Hide loading indicator
                    Toast.makeText(context, "Error: ${result.error}", Toast.LENGTH_SHORT).show() // Show error message
                }
            }
        })
    }

    private fun populateProfile(userResponse: UserResponse) {
        binding.tvUsername.text = userResponse.data?.name ?: "No Name"
        binding.tvEmail.text = userResponse.data?.email ?: "No Email"

        val profilePhotoUrl = userResponse.data?.photo
        if (!profilePhotoUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(profilePhotoUrl)
                .into(binding.profil)
        } else {
            Glide.with(this)
                .load(R.drawable.baseline_account_circle_24)
                .into(binding.profil)
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logout.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Konfirmasi")
                .setMessage("Apakah Anda yakin ingin keluar?")
                .setPositiveButton("Ya") { dialog, id ->
                    profileViewModel.logout()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }
                .setNegativeButton("Tidak") { dialog, id ->
                    dialog.dismiss()
                }

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.imageView.visibility = View.GONE
            binding.imageView2.visibility = View.GONE
            binding.textView.visibility = View.GONE
            binding.layoutSetting.visibility = View.GONE
            binding.layoutQuestion.visibility = View.GONE
            binding.setting.visibility = View.GONE
            binding.logout.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.imageView.visibility = View.VISIBLE
            binding.imageView2.visibility = View.VISIBLE
            binding.textView.visibility = View.VISIBLE
            binding.layoutSetting.visibility = View.VISIBLE
            binding.layoutQuestion.visibility = View.VISIBLE
            binding.setting.visibility = View.VISIBLE
            binding.logout.visibility = View.VISIBLE
        }
    }
}
