package com.nashirrestafauzian0083.todolist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String,
    val status: String,
    val statusColor: Long,
    val isDeleted: Boolean = false,
)
