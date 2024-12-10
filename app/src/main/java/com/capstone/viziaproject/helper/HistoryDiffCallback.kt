package com.capstone.viziaproject.helper

import androidx.recyclerview.widget.DiffUtil
import com.capstone.viziaproject.data.database.History

class HistoryDiffCallback (private val oldNoteList: List<History>, private val newNoteList: List<History>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldNoteList.size
    override fun getNewListSize(): Int = newNoteList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldNoteList[oldItemPosition].id == newNoteList[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldNote = oldNoteList[oldItemPosition]
        val newNote = newNoteList[newItemPosition]
        return oldNote.id == newNote.id && oldNote.predictionResult == newNote.predictionResult
    }
}