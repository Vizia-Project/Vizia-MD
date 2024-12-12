package com.capstone.viziaproject.ui.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.viziaproject.data.pref.UserPreference
import com.capstone.viziaproject.data.response.DataHistoryDetail
import com.capstone.viziaproject.data.response.DataItemHistory
import com.capstone.viziaproject.data.response.DetailHistoryResponse
import com.capstone.viziaproject.data.retrofit.ApiConfig
import com.capstone.viziaproject.databinding.ItemArtikelBinding
import com.capstone.viziaproject.databinding.ItemNewsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SaveAdapter(
    private val userPreference: UserPreference // Inject UserPreference for API calls
) : ListAdapter<DataHistoryDetail, SaveAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private var onItemClickCallback: OnItemClickCallback? = null

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataHistoryDetail>() {
            override fun areItemsTheSame(oldItem: DataHistoryDetail, newItem: DataHistoryDetail): Boolean {
                return oldItem.id == newItem.id // Use unique identifier
            }

            override fun areContentsTheSame(oldItem: DataHistoryDetail, newItem: DataHistoryDetail): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(detail: DataHistoryDetail)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemNewsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val historyItem = getItem(position)
        holder.bind(historyItem)
        holder.itemView.setOnClickListener {
            fetchDetailData(historyItem.id) { detail ->
                detail?.let { onItemClickCallback?.onItemClicked(it) }
            }
        }
    }

    private fun fetchDetailData(id: Int, callback: (DataHistoryDetail?) -> Unit) {
        // Coroutine scope for network operation
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val apiService = ApiConfig.getApiService(userPreference)
                val response = apiService.getDetailHistory(id)
                withContext(Dispatchers.Main) {
                    callback(response.data)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    callback(null)
                }
            }
        }
    }

    class MyViewHolder(private val binding: ItemNewsBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(historyItem: DataHistoryDetail) {
            val answers = historyItem.questionResult
            if (answers.isNotEmpty()) {
                fun getAnswerText(value: Int): String = when (value) {
                    0 -> "Tidak"
                    1 -> "Ya"
                    else -> "Invalid"
                }

                binding.tvCase1.text = getAnswerText(answers.getOrNull(0) ?: -1)
                binding.tvCase2.text = getAnswerText(answers.getOrNull(1) ?: -1)
                binding.tvCase3.text = getAnswerText(answers.getOrNull(2) ?: -1)
                binding.tvCase4.text = getAnswerText(answers.getOrNull(3) ?: -1)
                binding.tvCase5.text = getAnswerText(answers.getOrNull(4) ?: -1)
                binding.tvCase6.text = getAnswerText(answers.getOrNull(5) ?: -1)
                binding.tvCase7.text = getAnswerText(answers.getOrNull(6) ?: -1)
            }
            binding.tvName.text = historyItem.predictionResult
            binding.tvSummary.text = "%.2f".format(historyItem.accuracy) +"%"
            binding.tvDate.text = historyItem.date
            Glide.with(itemView.context)
                .load(historyItem.image)
                .into(binding.imgEvent)
        }
    }
}
