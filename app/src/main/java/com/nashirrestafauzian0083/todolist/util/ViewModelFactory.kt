package com.nashirrestafauzian0083.todolist.util

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nashirrestafauzian0083.todolist.database.TodoDb
import com.nashirrestafauzian0083.todolist.ui.screen.AddTodoViewModel
import com.nashirrestafauzian0083.todolist.ui.screen.TodoListViewModel
import com.nashirrestafauzian0083.todolist.ui.screen.TrashViewModel

class ViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dao = TodoDb.getInstance(context).dao
        if (modelClass.isAssignableFrom(TodoListViewModel::class.java)) {
            return TodoListViewModel(dao) as T
        } else if (modelClass.isAssignableFrom(AddTodoViewModel::class.java)) {
            return AddTodoViewModel(dao) as T
        } else if (modelClass.isAssignableFrom(TrashViewModel::class.java)) {
            return TrashViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
