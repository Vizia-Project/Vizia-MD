package com.capstone.viziaproject.data.database
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "histories")
@Parcelize
data class History(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

//    @ColumnInfo(name = "historyId")
//    var historyId: Int = 0,

    @ColumnInfo(name = "userId")
    var userId: Int = 0,

    @ColumnInfo(name = "date")
    var date: String? = null,

    @ColumnInfo(name = "image")
    val image: String? = null,

    @ColumnInfo(name = "infectionStatus")
    val infectionStatus: String? = null,

    @ColumnInfo(name = "q1")
    val q1: Int? = 0,

    @ColumnInfo(name = "q2")
    val q2: Int? = 0,

    @ColumnInfo(name = "q3")
    val q3: Int? = 0,

    @ColumnInfo(name = "q4")
    val q4: Int? = 0,

    @ColumnInfo(name = "q5")
    val q5: Int? = 0,

    @ColumnInfo(name = "q6")
    val q6: Int? = 0,

    @ColumnInfo(name = "q7")
    val q7: Int? = 0,

    @ColumnInfo(name = "predictionResult")
    val predictionResult: String? = null,

    @ColumnInfo(name = "accuracy")
    val accuracy: Double = 0.0,

    @ColumnInfo(name = "information")
    val information: String? = null

): Parcelable
