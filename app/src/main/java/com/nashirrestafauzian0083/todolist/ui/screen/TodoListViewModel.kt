package com.nashirrestafauzian0083.todolist.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nashirrestafauzian0083.todolist.database.TodoDao
import com.nashirrestafauzian0083.todolist.model.TodoItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TodoListViewModel(dao: TodoDao) : ViewModel() {

    val data: StateFlow<List<TodoItem>> = dao.getAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
}
