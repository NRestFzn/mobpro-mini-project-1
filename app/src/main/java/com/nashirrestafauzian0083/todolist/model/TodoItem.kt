package com.nashirrestafauzian0083.todolist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.squareup.moshi.Json

@Entity(tableName = "todo")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String,
    val status: String,
    @Json(name = "statuscolor")
    val statusColor: Long,
    @Json(name = "isdeleted")
    val isDeleted: Boolean = false,
    @Json(name = "image_url")
    val imageUrl: String? = null,
)
