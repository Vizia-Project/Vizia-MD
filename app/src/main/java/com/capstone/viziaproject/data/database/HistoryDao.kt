package com.capstone.viziaproject.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(history: History)

    @Update
    fun update(history: History)

    @Delete
    fun delete(history: History)

    @Query("SELECT * from histories WHERE userId = :userId ORDER BY id DESC")
    fun getAllHistory(userId: Int): LiveData<List<History?>>

    @Query("SELECT * FROM histories WHERE id = :id LIMIT 1")
    fun getHistoryById(id: Int): History?

}