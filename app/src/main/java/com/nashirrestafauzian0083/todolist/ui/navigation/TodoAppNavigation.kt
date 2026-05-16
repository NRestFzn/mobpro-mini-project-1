package com.nashirrestafauzian0083.todolist.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nashirrestafauzian0083.todolist.ui.screen.AboutScreen
import com.nashirrestafauzian0083.todolist.ui.screen.AddTodoScreen
import com.nashirrestafauzian0083.todolist.ui.screen.TodoListScreen
import com.nashirrestafauzian0083.todolist.ui.screen.TrashScreen

@Composable
fun TodoAppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Todo.route,
    ) {
        composable(Screen.Todo.route) {
            TodoListScreen(
                onAddClick = { navController.navigate(Screen.AddTodo.route) },
                onItemClick = { id -> navController.navigate(Screen.EditTodo.withId(id)) },
                onAboutClick = { navController.navigate(Screen.About.route) },
                onTrashClick = { navController.navigate(Screen.Trash.route) },
            )
        }
        composable(Screen.AddTodo.route) {
            AddTodoScreen(
                id = null,
                onNavigateUp = { navController.popBackStack() },
            )
        }
        composable(
            route = Screen.EditTodo.route,
            arguments = listOf(navArgument(KEY_ID_TODO) { type = NavType.LongType }),
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong(KEY_ID_TODO)
            AddTodoScreen(
                id = id,
                onNavigateUp = { navController.popBackStack() },
            )
        }
        composable(Screen.Trash.route) {
            TrashScreen(
                onNavigateUp = { navController.popBackStack() },
                onItemClick = { id -> navController.navigate(Screen.EditTodo.withId(id)) },
            )
        }
        composable(Screen.About.route) {
            AboutScreen(onNavigateUp = { navController.popBackStack() })
        }
    }
}
