package com.nashirrestafauzian0083.todolist.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nashirrestafauzian0083.todolist.R
import com.nashirrestafauzian0083.todolist.TodoItem
import com.nashirrestafauzian0083.todolist.ui.screen.AboutScreen
import com.nashirrestafauzian0083.todolist.ui.screen.AddTodoScreen
import com.nashirrestafauzian0083.todolist.ui.screen.TodoListScreen

private val TodoListSaver = listSaver<SnapshotStateList<TodoItem>, String>(
    save = { items ->
        items.flatMap { item ->
            listOf(
                item.id.toString(),
                item.title,
                item.description,
                item.isImportant.toString(),
            )
        }
    },
    restore = { values ->
        mutableStateListOf<TodoItem>().apply {
            values.chunked(4).forEach { chunk ->
                if (chunk.size == 4) {
                    add(
                        TodoItem(
                            id = chunk[0].toInt(),
                            title = chunk[1],
                            description = chunk[2],
                            isImportant = chunk[3].toBoolean(),
                        )
                    )
                }
            }
        }
    },
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoAppNavigation() {
    val navController = rememberNavController()
    val todoItems = rememberSaveable(saver = TodoListSaver) {
        mutableStateListOf<TodoItem>()
    }

    var nextTodoId by rememberSaveable { mutableIntStateOf(1) }
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var isImportant by rememberSaveable { mutableStateOf(false) }
    var titleError by rememberSaveable { mutableStateOf(false) }
    var descriptionError by rememberSaveable { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Todo.route
    val currentScreen = currentScreen(currentRoute)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(currentScreen.titleRes)) },
                navigationIcon = {
                    if (currentScreen != Screen.Todo) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.up_label),
                            )
                        }
                    }
                },
                actions = {
                    if (currentScreen != Screen.About) {
                        TextButton(
                            onClick = {
                                navController.navigate(Screen.About.route) {
                                    launchSingleTop = true
                                }
                            }
                        ) {
                            Text(text = stringResource(R.string.about_menu_title))
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Todo.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            composable(Screen.Todo.route) {
                TodoListScreen(
                    todoItems = todoItems,
                    onAddTodoClick = { navController.navigate(Screen.AddTodo.route) },
                )
            }

            composable(Screen.AddTodo.route) {
                AddTodoScreen(
                    title = title,
                    description = description,
                    isImportant = isImportant,
                    titleError = titleError,
                    descriptionError = descriptionError,
                    onTitleChange = {
                        title = it
                        if (it.isNotBlank()) {
                            titleError = false
                        }
                    },
                    onDescriptionChange = {
                        description = it
                        if (it.isNotBlank()) {
                            descriptionError = false
                        }
                    },
                    onImportantChange = { isImportant = it },
                    onSaveClick = {
                        titleError = title.isBlank()
                        descriptionError = description.isBlank()

                        if (!titleError && !descriptionError) {
                            todoItems.add(
                                index = 0,
                                element = TodoItem(
                                    id = nextTodoId,
                                    title = title.trim(),
                                    description = description.trim(),
                                    isImportant = isImportant,
                                ),
                            )
                            nextTodoId += 1
                            title = ""
                            description = ""
                            isImportant = false
                            titleError = false
                            descriptionError = false
                            navigateToTodo(navController)
                        }
                    },
                )
            }

            composable(Screen.About.route) {
                AboutScreen()
            }
        }
    }
}

private fun currentScreen(route: String): Screen {
    return when (route) {
        Screen.AddTodo.route -> Screen.AddTodo
        Screen.About.route -> Screen.About
        else -> Screen.Todo
    }
}

private fun navigateToTodo(navController: NavHostController) {
    navController.navigate(Screen.Todo.route) {
        popUpTo(Screen.Todo.route) {
            inclusive = false
        }
        launchSingleTop = true
    }
}
