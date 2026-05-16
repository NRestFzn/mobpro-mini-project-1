package com.nashirrestafauzian0083.todolist.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nashirrestafauzian0083.todolist.database.TodoDao
import com.nashirrestafauzian0083.todolist.model.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTodoViewModel(private val dao: TodoDao) : ViewModel() {

    suspend fun getTodo(id: Long): TodoItem? = dao.getById(id)

    fun insert(title: String, description: String, status: String, statusColor: Long) {
        val todo = TodoItem(
            title = title,
            description = description,
            status = status,
            statusColor = statusColor,
        )
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(todo)
        }
    }

    fun update(id: Long, title: String, description: String, status: String, statusColor: Long) {
        val todo = TodoItem(
            id = id,
            title = title,
            description = description,
            status = status,
            statusColor = statusColor,
        )
        viewModelScope.launch(Dispatchers.IO) {
            dao.update(todo)
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.softDeleteById(id)
        }
    }

    fun deletePermanently(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.hardDeleteById(id)
        }
    }
}
