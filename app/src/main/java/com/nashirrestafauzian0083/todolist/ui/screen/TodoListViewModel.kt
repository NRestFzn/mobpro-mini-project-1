package com.nashirrestafauzian0083.todolist.ui.screen

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nashirrestafauzian0083.todolist.model.TodoItem
import com.nashirrestafauzian0083.todolist.network.ApiStatus
import com.nashirrestafauzian0083.todolist.network.TodoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TodoListViewModel : ViewModel() {

    var data = mutableStateOf(emptyList<TodoItem>())
        private set
    var status = MutableStateFlow(ApiStatus.LOADING)
        private set
    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun retrieveData(userEmail: String) {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                val response = TodoApi.service.getActiveTodos(userEmail)
                if (response.error != null) {
                    throw Exception(response.error)
                }
                data.value = response.data ?: emptyList()
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.d("TodoListViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun softDelete(userEmail: String, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = TodoApi.service.softDeleteTodo(userEmail, id)
                if (response.error != null) throw Exception(response.error)
                retrieveData(userEmail) // Refresh data
            } catch (e: Exception) {
                Log.d("TodoListViewModel", "SoftDelete Failure: ${e.message}")
                errorMessage.value = "Failed to delete: ${e.message}"
            }
        }
    }

    fun clearMessage() {
        errorMessage.value = null
    }

    fun clearData() {
        data.value = emptyList()
        status.value = ApiStatus.LOADING
    }
}
