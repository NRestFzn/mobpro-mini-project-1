package com.nashirrestafauzian0083.todolist.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nashirrestafauzian0083.todolist.ui.screen.AddTodoViewModel
import com.nashirrestafauzian0083.todolist.ui.screen.TodoListViewModel
import com.nashirrestafauzian0083.todolist.ui.screen.TrashViewModel

class ViewModelFactory(
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoListViewModel::class.java)) {
            return TodoListViewModel() as T
        } else if (modelClass.isAssignableFrom(AddTodoViewModel::class.java)) {
            return AddTodoViewModel() as T
        } else if (modelClass.isAssignableFrom(TrashViewModel::class.java)) {
            return TrashViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
