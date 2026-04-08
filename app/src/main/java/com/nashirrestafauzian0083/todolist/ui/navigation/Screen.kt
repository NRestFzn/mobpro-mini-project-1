package com.nashirrestafauzian0083.todolist.ui.navigation

import com.nashirrestafauzian0083.todolist.R

sealed class Screen(val route: String, val titleRes: Int) {
    data object Todo : Screen("todo", R.string.todo_menu_title)
    data object AddTodo : Screen("add_todo", R.string.add_menu_title)
    data object About : Screen("about", R.string.about_menu_title)
}
