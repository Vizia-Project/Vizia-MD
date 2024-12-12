package com.capstone.viziaproject.ui.saveHistory

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.viziaproject.data.pref.UserPreference
import com.capstone.viziaproject.data.pref.dataStore
import com.capstone.viziaproject.data.response.DataHistoryDetail
import com.capstone.viziaproject.databinding.FragmentSaveBinding
import com.capstone.viziaproject.helper.ViewModelFactory
import com.capstone.viziaproject.ui.IntroActivity
import com.capstone.viziaproject.ui.history.DetailHistoryActivity
import com.capstone.viziaproject.ui.history.SaveAdapter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SaveFragment : Fragment() {
    private var _binding: FragmentSaveBinding? = null
    private val binding get() = _binding!!
//    private lateinit var adapter: SaveAdapter
    private lateinit var userPreference: UserPreference
    private val adapter: SaveAdapter by lazy { SaveAdapter(userPreference) }
    private var isToastShown = false

    private val viewModel: SaveViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSaveBinding.inflate(inflater, container, false)
        userPreference = UserPreference.getInstance(requireContext().dataStore)
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = LinearLayoutManager(context)
        binding.rvHistory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(context, layoutManager.orientation)
        binding.rvHistory.addItemDecoration(itemDecoration)
        binding.rvHistory.adapter = adapter

        adapter.setOnItemClickCallback(object : SaveAdapter.OnItemClickCallback {
            override fun onItemClicked(detail: DataHistoryDetail) {
                val intent = Intent(activity, DetailHistoryActivity::class.java)
                intent.putExtra("EVENT_ID", detail.id)
                intent.putExtra("EXTRA_HISTORY_DETAIL", detail)
                startActivity(intent)
            }
        })
        lifecycleScope.launch {
            val user = userPreference.getSession().first()
            if (user.token.isNotEmpty() && user.isLogin) {
                viewModel.getHistoryUser(user.userId).observe(viewLifecycleOwner) { users ->
                    if (!users.isNullOrEmpty()) {
                        setEventData(users.map {
                            DataHistoryDetail(
                                date = it?.date ?: "No Date",
                                image = it?.image ?: "",
                                infectionStatus = it?.infectionStatus ?: "Unknown",
                                questionResult = listOf(
                                    it?.q1 ?: -1,
                                    it?.q2 ?: -1,
                                    it?.q3 ?: -1,
                                    it?.q4 ?: -1,
                                    it?.q5 ?: -1,
                                    it?.q6 ?: -1,
                                    it?.q7 ?: -1
                                ),
                                predictionResult = it?.predictionResult ?: "No Result",
                                accuracy = it?.accuracy ?: 0.0,
                                information = it?.information ?: "No Info",
                                id = it?.id ?: -1,
                            )
                        })
                        binding.imgData.visibility = View.GONE
                    } else {
                        binding.rvHistory.visibility = View.GONE
                        binding.tvEvent.visibility = View.GONE
                        binding.imgData.visibility = View.VISIBLE
                    }
                }
            } else {
                startActivity(Intent(requireContext(), IntroActivity::class.java))
                requireActivity().finish()
            }
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showError(it)
                viewModel.clearError()
            }
        }
    }

    private fun showError(message: String) {
        if (!isToastShown) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            isToastShown = true
        }
    }


    private fun setEventData(events: List<DataHistoryDetail>) {
        adapter.submitList(events)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}