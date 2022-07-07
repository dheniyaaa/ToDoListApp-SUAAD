package com.dicoding.todoapp.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dicoding.todoapp.utils.TABLE
import kotlinx.parcelize.Parcelize

//TODO 1 : Define a local database table using the schema in app/schema/tasks.json
@Parcelize
@Entity(tableName = TABLE)
data class Task(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "description")
    var description: String,

    @ColumnInfo(name = "dueDate")
    var dueDateMillis: Long,

    @ColumnInfo(name = "completed")
    var isCompleted: Boolean = false
) : Parcelable
