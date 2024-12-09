package com.capstone.viziaproject.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.viziaproject.data.response.DataItem
import com.capstone.viziaproject.data.response.DataItemHistory
import com.capstone.viziaproject.databinding.ItemArtikelBinding
import com.capstone.viziaproject.ui.home.HomeAdapter

class HistoryAdapter: ListAdapter<DataItemHistory, HistoryAdapter.MyViewHolder>(DIFF_CALLBACK) {
    private var onItemClickCallback: OnItemClickCallback? = null

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItemHistory>() {
            override fun areItemsTheSame(oldItem: DataItemHistory, newItem: DataItemHistory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: DataItemHistory, newItem: DataItemHistory): Boolean {
                return oldItem == newItem
            }
        }
    }
    interface OnItemClickCallback {
        fun onItemClicked(data: DataItemHistory)
    }

    fun setOnItemClickCallback(onItemClickCallback: HistoryAdapter.OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemArtikelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)

        holder.itemView.setOnClickListener {
            onItemClickCallback?.onItemClicked(event)
        }
    }

    class MyViewHolder(private val binding: ItemArtikelBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(news: DataItemHistory) {
            binding.tvName.text = news.predictionResult
            binding.tvSummary.text = news.accuracy.toString()
            Glide.with(itemView.context)
                .load(news.image)
                .into(binding.imgEvent)
        }
    }
}