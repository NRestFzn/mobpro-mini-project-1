package com.nashirrestafauzian0083.todolist.ui.screen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.runtime.DisposableEffect
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.nashirrestafauzian0083.todolist.BuildConfig
import com.nashirrestafauzian0083.todolist.R
import com.nashirrestafauzian0083.todolist.model.User
import com.nashirrestafauzian0083.todolist.network.ApiStatus
import com.nashirrestafauzian0083.todolist.util.SettingsDataStore
import com.nashirrestafauzian0083.todolist.util.UserDataStore
import com.nashirrestafauzian0083.todolist.util.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
    val factory = remember(context) { ViewModelFactory() }
    val viewModel: TodoListViewModel = viewModel(factory = factory)

    val dataStore = remember(context) { SettingsDataStore(context) }
    val scope = rememberCoroutineScope()
    val showList by dataStore.layoutFlow.collectAsState(initial = true)
    val isDark by dataStore.darkThemeFlow.collectAsState(initial = false)

    val userDataStore = remember(context) { UserDataStore(context) }
    val user by userDataStore.userFlow.collectAsState(initial = User())
    var showDialog by remember { mutableStateOf(false) }

    val data by viewModel.data
    val status by viewModel.status.collectAsState()
    val errorMessage by viewModel.errorMessage

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, user.email) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (user.email.isNotEmpty()) {
                    viewModel.retrieveData(user.email)
                } else {
                    viewModel.clearData()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(user.email) {
        if (user.email.isEmpty()) {
            viewModel.clearData()
        }
    }

    if (errorMessage != null) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        viewModel.clearMessage()
    }

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
                        if(user.email.isEmpty()) {
                            CoroutineScope(Dispatchers.IO).launch { signIn(context, userDataStore) }
                        } else {
                            showDialog = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = stringResource(R.string.profil),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
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
            FloatingActionButton(onClick = {
                if (user.email.isNotEmpty()) {
                    onAddClick()
                } else {
                    Toast.makeText(context, context.getString(R.string.please_login), Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.add_menu_title),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
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
                    emptyTitleRes = R.string.empty_todo_title,
                    emptyDescriptionRes = R.string.empty_todo_description,
                    modifier = Modifier.padding(innerPadding),
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

        if(showDialog) {
            ProfilDialog(
                user = user,
                onDismissRequest = { showDialog = false }
            ) {
                CoroutineScope(Dispatchers.IO).launch { 
                    signout(context, userDataStore)
                    kotlinx.coroutines.withContext(Dispatchers.Main) {
                        Toast.makeText(context, context.getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
                    }
                }
                showDialog = false
            }
        }
    }
}

private suspend fun signIn(context: Context, dataStore: UserDataStore) {
    val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(BuildConfig.API_KEY)
        .build()

    val request: GetCredentialRequest = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    try {
        val credentialManager = CredentialManager.create(context)
        val result = credentialManager.getCredential(context, request)
        handleSignIn(result, dataStore)
    } catch (e: androidx.credentials.exceptions.NoCredentialException) {
        Log.e("SIGN-IN", "Error: No credentials found.")
    } catch (e: GetCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}

private suspend fun handleSignIn(result: GetCredentialResponse, dataStore: UserDataStore) {
    val credential = result.credential
    if(credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        try {
            val googleId = GoogleIdTokenCredential.createFrom(credential.data)
            val nama = googleId.displayName ?: ""
            val email = googleId.email ?: ""
            val photoUrl = googleId.profilePictureUri?.toString() ?: ""

            dataStore.saveData(User(nama, email, photoUrl))
        } catch(e: GoogleIdTokenParsingException) {
            Log.e("SIGN-IN, ","Error: ${e.message}")
        }
    }
}

private suspend fun signout(context: Context, dataStore: UserDataStore) {
    try {
        val credentialManager = CredentialManager.create(context)
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        dataStore.saveData(User())
    } catch (e: ClearCredentialException) {
        Log.e("SIGN-IN", "Error: ${e.errorMessage}")
    }
}
