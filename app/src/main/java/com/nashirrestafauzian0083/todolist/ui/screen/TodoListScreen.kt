package com.nashirrestafauzian0083.todolist.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nashirrestafauzian0083.todolist.R
import com.nashirrestafauzian0083.todolist.util.SettingsDataStore
import com.nashirrestafauzian0083.todolist.util.ViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    onAddClick: () -> Unit,
    onItemClick: (Long) -> Unit,
    onAboutClick: () -> Unit,
    onTrashClick: () -> Unit,
) {
    val context = LocalContext.current
    val factory = remember(context) { ViewModelFactory(context) }
    val viewModel: TodoListViewModel = viewModel(factory = factory)
    val data by viewModel.data.collectAsState()

    val dataStore = remember(context) { SettingsDataStore(context) }
    val scope = rememberCoroutineScope()
    val showList by dataStore.layoutFlow.collectAsState(initial = true)
    val isDark by dataStore.darkThemeFlow.collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.todo_menu_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(onClick = {
                        scope.launch { dataStore.saveDarkTheme(!isDark) }
                    }) {
                        Icon(
                            imageVector = if (isDark) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                            contentDescription = stringResource(
                                if (isDark) R.string.light_theme else R.string.dark_theme
                            ),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    IconButton(onClick = {
                        scope.launch { dataStore.saveLayout(!showList) }
                    }) {
                        Icon(
                            imageVector = if (showList) Icons.Filled.GridView else Icons.AutoMirrored.Filled.ViewList,
                            contentDescription = stringResource(
                                if (showList) R.string.grid_view else R.string.list_view
                            ),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    IconButton(onClick = onTrashClick) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = stringResource(R.string.trash_menu_title),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    IconButton(onClick = onAboutClick) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = stringResource(R.string.about_menu_title),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_menu_title),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        },
    ) { innerPadding ->
        TodoCollection(
            data = data,
            showList = showList,
            onItemClick = onItemClick,
            emptyTitleRes = R.string.empty_todo_title,
            emptyDescriptionRes = R.string.empty_todo_description,
            modifier = Modifier.padding(innerPadding),
        )
    }
}
