package com.nashirrestafauzian0083.todolist.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nashirrestafauzian0083.todolist.model.TodoItem
import com.nashirrestafauzian0083.todolist.network.TodoApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class AddTodoViewModel : ViewModel() {

    suspend fun getTodo(userEmail: String, id: Long): TodoItem? {
        return try {
            val activeResponse = TodoApi.service.getActiveTodos(userEmail)
            var todo = activeResponse.data?.find { it.id == id }
            if (todo == null) {
                val deletedResponse = TodoApi.service.getDeletedTodos(userEmail)
                todo = deletedResponse.data?.find { it.id == id }
            }
            todo
        } catch (e: Exception) {
            Log.d("AddTodoViewModel", "Failure: ${e.message}")
            null
        }
    }

    fun insert(userEmail: String, title: String, description: String, status: String, statusColor: Long, bitmap: android.graphics.Bitmap?, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val descBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
                val statusBody = status.toRequestBody("text/plain".toMediaTypeOrNull())
                val colorBody = statusColor.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                
                var imagePart: okhttp3.MultipartBody.Part? = null
                if (bitmap != null) {
                    val stream = java.io.ByteArrayOutputStream()
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, stream)
                    val byteArray = stream.toByteArray()
                    val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    imagePart = okhttp3.MultipartBody.Part.createFormData("image", "image.jpg", requestBody)
                }

                TodoApi.service.createTodo(userEmail, titleBody, descBody, statusBody, colorBody, imagePart)
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.d("AddTodoViewModel", "Insert Failure: ${e.message}")
            }
        }
    }

    fun update(userEmail: String, id: Long, title: String, description: String, status: String, statusColor: Long, bitmap: android.graphics.Bitmap?, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val descBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
                val statusBody = status.toRequestBody("text/plain".toMediaTypeOrNull())
                val colorBody = statusColor.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                
                var imagePart: okhttp3.MultipartBody.Part? = null
                if (bitmap != null) {
                    val stream = java.io.ByteArrayOutputStream()
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, stream)
                    val byteArray = stream.toByteArray()
                    val requestBody = byteArray.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    imagePart = okhttp3.MultipartBody.Part.createFormData("image", "image.jpg", requestBody)
                }

                TodoApi.service.updateTodo(userEmail, id, titleBody, descBody, statusBody, colorBody, imagePart)
                TodoApi.service.restoreTodo(userEmail, id)
                kotlinx.coroutines.withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                Log.d("AddTodoViewModel", "Update Failure: ${e.message}")
            }
        }
    }

    fun delete(userEmail: String, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                TodoApi.service.softDeleteTodo(userEmail, id)
            } catch (e: Exception) {
                Log.d("AddTodoViewModel", "Delete Failure: ${e.message}")
            }
        }
    }

    fun deletePermanently(userEmail: String, id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                TodoApi.service.hardDeleteTodo(userEmail, id)
            } catch (e: Exception) {
                Log.d("AddTodoViewModel", "Permanent Delete Failure: ${e.message}")
            }
        }
    }
}
