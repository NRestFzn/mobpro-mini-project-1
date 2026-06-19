package com.nashirrestafauzian0083.todolist.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nashirrestafauzian0083.todolist.R
import com.nashirrestafauzian0083.todolist.model.User
import com.nashirrestafauzian0083.todolist.network.ApiStatus
import com.nashirrestafauzian0083.todolist.util.SettingsDataStore
import com.nashirrestafauzian0083.todolist.util.UserDataStore
import com.nashirrestafauzian0083.todolist.util.ViewModelFactory
import kotlinx.coroutines.launch
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.DisposableEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(
    onNavigateUp: () -> Unit,
    onItemClick: (Long) -> Unit,
) {
    val context = LocalContext.current
    val factory = remember(context) { ViewModelFactory() }
    val viewModel: TrashViewModel = viewModel(factory = factory)

    val dataStore = remember(context) { SettingsDataStore(context) }
    val scope = rememberCoroutineScope()
    val showList by dataStore.layoutFlow.collectAsState(initial = true)

    val userDataStore = remember(context) { UserDataStore(context) }
    val user by userDataStore.userFlow.collectAsState(initial = User())

    val data by viewModel.data
    val status by viewModel.status.collectAsState()
    val errorMessage by viewModel.errorMessage

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, user.email) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && user.email.isNotEmpty()) {
                viewModel.retrieveData(user.email)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (errorMessage != null) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        viewModel.clearMessage()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.up_label),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                title = { Text(text = stringResource(R.string.trash_menu_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
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
                },
            )
        },
    ) { innerPadding ->
        when (status) {
            ApiStatus.LOADING -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    if (user.email.isEmpty()) {
                        Text(text = stringResource(R.string.please_login))
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
            ApiStatus.SUCCESS -> {
                TodoCollection(
                    data = data,
                    showList = showList,
                    onItemClick = onItemClick,
                    emptyTitleRes = R.string.empty_trash_title,
                    emptyDescriptionRes = R.string.empty_trash_description,
                    modifier = Modifier.padding(innerPadding),
                    bottomPadding = 16,
                )
            }
            ApiStatus.FAILED -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = stringResource(R.string.error))
                    Button(
                        onClick = { viewModel.retrieveData(user.email) },
                        modifier = Modifier.padding(16.dp),
                        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp),
                    ) {
                        Text(text = stringResource(R.string.try_again))
                    }
                }
            }
        }
    }
}
