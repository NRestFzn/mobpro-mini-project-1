package com.nashirrestafauzian0083.todolist.ui.navigation

const val KEY_ID_TODO = "idTodo"

sealed class Screen(val route: String) {
    data object Todo : Screen("todo")
    data object AddTodo : Screen("add_todo")
    data object EditTodo : Screen("add_todo/{$KEY_ID_TODO}") {
        fun withId(id: Long) = "add_todo/$id"
    }
    data object About : Screen("about")
    data object Trash : Screen("trash")
}
