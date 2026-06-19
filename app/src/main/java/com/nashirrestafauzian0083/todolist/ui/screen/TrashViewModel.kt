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

class TrashViewModel : ViewModel() {

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
                val response = TodoApi.service.getDeletedTodos(userEmail)
                if (response.error != null) {
                    throw Exception(response.error)
                }
                data.value = response.data ?: emptyList()
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.d("TrashViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun clearMessage() {
        errorMessage.value = null
    }

}
