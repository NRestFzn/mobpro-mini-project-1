package com.nashirrestafauzian0083.todolist.network

import com.nashirrestafauzian0083.todolist.model.TodoItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

private const val BASE_URL = "https://mobpro1-api.vercel.app/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

data class ApiResponse(
    val data: List<TodoItem>? = null,
    val error: String? = null
)

data class SingleApiResponse(
    val data: TodoItem? = null,
    val error: String? = null
)

interface TodoApiService {
    @GET("api/todos")
    suspend fun getActiveTodos(
        @Header("Authorization") userEmail: String
    ): ApiResponse

    @GET("api/todos/deleted")
    suspend fun getDeletedTodos(
        @Header("Authorization") userEmail: String
    ): ApiResponse

    @retrofit2.http.Multipart
    @POST("api/todos")
    suspend fun createTodo(
        @Header("Authorization") userEmail: String,
        @retrofit2.http.Part("title") title: okhttp3.RequestBody,
        @retrofit2.http.Part("description") description: okhttp3.RequestBody,
        @retrofit2.http.Part("status") status: okhttp3.RequestBody,
        @retrofit2.http.Part("statuscolor") statusColor: okhttp3.RequestBody,
        @retrofit2.http.Part image: okhttp3.MultipartBody.Part? = null
    ): SingleApiResponse

    @retrofit2.http.Multipart
    @PUT("api/todos/{id}")
    suspend fun updateTodo(
        @Header("Authorization") userEmail: String,
        @Path("id") id: Long,
        @retrofit2.http.Part("title") title: okhttp3.RequestBody,
        @retrofit2.http.Part("description") description: okhttp3.RequestBody,
        @retrofit2.http.Part("status") status: okhttp3.RequestBody,
        @retrofit2.http.Part("statuscolor") statusColor: okhttp3.RequestBody,
        @retrofit2.http.Part image: okhttp3.MultipartBody.Part? = null
    ): SingleApiResponse

    @PATCH("api/todos/{id}/soft-delete")
    suspend fun softDeleteTodo(
        @Header("Authorization") userEmail: String,
        @Path("id") id: Long
    ): SingleApiResponse

    @PATCH("api/todos/{id}/restore")
    suspend fun restoreTodo(
        @Header("Authorization") userEmail: String,
        @Path("id") id: Long
    ): SingleApiResponse

    @DELETE("api/todos/{id}")
    suspend fun hardDeleteTodo(
        @Header("Authorization") userEmail: String,
        @Path("id") id: Long
    )
}

object TodoApi {
    val service: TodoApiService by lazy {
        retrofit.create(TodoApiService::class.java)
    }
}

enum class ApiStatus { LOADING, SUCCESS, FAILED }
