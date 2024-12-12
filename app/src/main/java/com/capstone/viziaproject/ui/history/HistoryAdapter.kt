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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoryAdapter(
    private val userPreference: UserPreference // Inject UserPreference for API calls
) : ListAdapter<DataItemHistory, HistoryAdapter.MyViewHolder>(DIFF_CALLBACK) {

    private var onItemClickCallback: OnItemClickCallback? = null

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItemHistory>() {
            override fun areItemsTheSame(oldItem: DataItemHistory, newItem: DataItemHistory): Boolean {
                return oldItem.id == newItem.id // Use unique identifier
            }

            override fun areContentsTheSame(oldItem: DataItemHistory, newItem: DataItemHistory): Boolean {
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
        val binding = ItemArtikelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    class MyViewHolder(private val binding: ItemArtikelBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(historyItem: DataItemHistory) {
            binding.tvName.text = historyItem.predictionResult
            binding.tvSummary.text = "%.2f".format(historyItem.accuracy)
            Glide.with(itemView.context)
                .load(historyItem.image)
                .into(binding.imgEvent)
        }
    }
}
