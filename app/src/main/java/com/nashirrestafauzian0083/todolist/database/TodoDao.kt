package com.nashirrestafauzian0083.todolist.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.nashirrestafauzian0083.todolist.model.TodoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Insert
    suspend fun insert(todo: TodoItem)

    @Update
    suspend fun update(todo: TodoItem)

    @Query("SELECT * FROM todo WHERE isDeleted = 0 ORDER BY id DESC")
    fun getAll(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo WHERE isDeleted = 1 ORDER BY id DESC")
    fun getDeleted(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo WHERE id = :id")
    suspend fun getById(id: Long): TodoItem?

    @Query("UPDATE todo SET isDeleted = 1 WHERE id = :id")
    suspend fun softDeleteById(id: Long)

    @Query("UPDATE todo SET isDeleted = 0 WHERE id = :id")
    suspend fun restoreById(id: Long)

    @Query("DELETE FROM todo WHERE id = :id")
    suspend fun hardDeleteById(id: Long)
}
