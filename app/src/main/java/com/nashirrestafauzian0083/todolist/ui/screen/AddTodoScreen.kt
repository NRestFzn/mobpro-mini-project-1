package com.nashirrestafauzian0083.todolist.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.mutableLongStateOf
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.material.icons.filled.Add
import com.nashirrestafauzian0083.todolist.R
import com.nashirrestafauzian0083.todolist.util.ViewModelFactory

private val DefaultStatusColors: List<Long> = listOf(
    0xFFEF5350L, // red
    0xFFFFA726L, // orange
    0xFFFFEE58L, // yellow
    0xFF66BB6AL, // green
    0xFF42A5F5L, // blue
    0xFFAB47BCL, // purple
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTodoScreen(
    id: Long?,
    onNavigateUp: () -> Unit,
) {
    val context = LocalContext.current
    val factory = remember(context) { ViewModelFactory() }
    val viewModel: AddTodoViewModel = viewModel(factory = factory)

    val userDataStore = remember(context) { com.nashirrestafauzian0083.todolist.util.UserDataStore(context) }
    val user by userDataStore.userFlow.collectAsState(initial = com.nashirrestafauzian0083.todolist.model.User())

    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var status by rememberSaveable { mutableStateOf("") }
    var statusColor by rememberSaveable { mutableLongStateOf(DefaultStatusColors[0]) }
    var isDeleted by rememberSaveable { mutableStateOf(false) }
    var imageUrl by rememberSaveable { mutableStateOf<String?>(null) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var titleError by rememberSaveable { mutableStateOf(false) }
    var descriptionError by rememberSaveable { mutableStateOf(false) }
    var statusError by rememberSaveable { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var isSaving by rememberSaveable { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            val uri = result.uriContent
            if (uri != null) {
                bitmap = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                }
            }
        } else {
            Log.e("IMAGE", "Error: ${result.error}")
        }
    }

    LaunchedEffect(id, user.email) {
        if (id == null || user.email.isEmpty()) return@LaunchedEffect
        val todo = viewModel.getTodo(user.email, id) ?: return@LaunchedEffect
        title = todo.title
        description = todo.description
        status = todo.status
        statusColor = todo.statusColor
        isDeleted = todo.isDeleted
        imageUrl = todo.imageUrl
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
                title = {
                    Text(
                        text = stringResource(
                            if (id == null) R.string.add_menu_title
                            else R.string.edit_menu_title
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                actions = {
                    IconButton(
                        onClick = {
                            if (isSaving) return@IconButton
                            titleError = title.isBlank()
                            descriptionError = description.isBlank()
                            statusError = status.isBlank()
                            if (titleError || descriptionError || statusError) return@IconButton

                            isSaving = true
                            if (id == null) {
                                viewModel.insert(user.email, title, description, status, statusColor, bitmap) {
                                    isSaving = false
                                    onNavigateUp()
                                }
                            } else {
                                viewModel.update(user.email, id, title, description, status, statusColor, bitmap) {
                                    isSaving = false
                                    onNavigateUp()
                                }
                            }
                        },
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = stringResource(R.string.save_todo_button),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    if (id != null) {
                        DeleteAction(permanent = isDeleted) { }
                    }
                },
            )
        },
    ) { innerPadding ->
        FormTodo(
            title = title,
            onTitleChange = {
                title = it
                if (it.isNotBlank()) titleError = false
            },
            titleError = titleError,
            description = description,
            onDescriptionChange = {
                description = it
                if (it.isNotBlank()) descriptionError = false
            },
            descriptionError = descriptionError,
            status = status,
            onStatusChange = {
                status = it
                if (it.isNotBlank()) statusError = false
            },
            statusError = statusError,
            statusColor = statusColor,
            onStatusColorChange = { statusColor = it },
            bitmap = bitmap,
            imageUrl = imageUrl,
            onImageClick = {
                val options = CropImageContractOptions(
                    null,
                    CropImageOptions(
                        imageSourceIncludeGallery = true,
                        imageSourceIncludeCamera = true,
                        fixAspectRatio = true
                    )
                )
                launcher.launch(options)
            },
            modifier = Modifier.padding(innerPadding),
        )

        if (id != null && showDialog) {
            DisplayAlertDialog(
                permanent = isDeleted,
                onDismissRequest = { showDialog = false },
                onConfirmation = {
                    showDialog = false
                    if (isDeleted) viewModel.deletePermanently(user.email, id) else viewModel.delete(user.email, id)
                    onNavigateUp()
                },
            )
        }
    }
}

@Composable
private fun FormTodo(
    title: String,
    onTitleChange: (String) -> Unit,
    titleError: Boolean,
    description: String,
    onDescriptionChange: (String) -> Unit,
    descriptionError: Boolean,
    status: String,
    onStatusChange: (String) -> Unit,
    statusError: Boolean,
    statusColor: Long,
    onStatusColorChange: (Long) -> Unit,
    bitmap: Bitmap?,
    imageUrl: String?,
    onImageClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        val imageModifier = Modifier
            .fillMaxWidth()
            .size(200.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .clickable { onImageClick() }
            .border(1.dp, MaterialTheme.colorScheme.outline, androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)

        if (bitmap != null) {
            androidx.compose.foundation.Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Preview Image",
                contentScale = ContentScale.Crop,
                modifier = imageModifier
            )
        } else if (imageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Todo Image",
                contentScale = ContentScale.Crop,
                modifier = imageModifier
            )
            } else {
            Box(
                modifier = imageModifier,
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Attach Image",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Attach Image",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text(text = stringResource(R.string.title_label)) },
            singleLine = true,
            isError = titleError,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        if (titleError) {
            Text(
                text = stringResource(R.string.title_error),
                color = MaterialTheme.colorScheme.error,
            )
        }

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(text = stringResource(R.string.description_label)) },
            isError = descriptionError,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        if (descriptionError) {
            Text(
                text = stringResource(R.string.description_error),
                color = MaterialTheme.colorScheme.error,
            )
        }

        OutlinedTextField(
            value = status,
            onValueChange = onStatusChange,
            label = { Text(text = stringResource(R.string.status_label)) },
            placeholder = { Text(text = stringResource(R.string.status_placeholder)) },
            singleLine = true,
            isError = statusError,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        if (statusError) {
            Text(
                text = stringResource(R.string.status_error),
                color = MaterialTheme.colorScheme.error,
            )
        }

        Text(text = stringResource(R.string.status_color_label))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DefaultStatusColors.forEach { argb ->
                ColorSwatch(
                    argb = argb,
                    selected = argb == statusColor,
                    onClick = { onStatusColorChange(argb) },
                )
            }
        }
    }
}

@Composable
private fun ColorSwatch(argb: Long, selected: Boolean, onClick: () -> Unit) {
    val color = Color(argb.toInt())
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.outline,
                shape = CircleShape,
            )
            .clickable { onClick() },
    )
}

@Composable
private fun DeleteAction(permanent: Boolean, onDelete: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(R.string.more_options),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(
                            if (permanent) R.string.delete_todo_permanently
                            else R.string.delete_todo
                        )
                    )
                },
                onClick = {
                    expanded = false
                    onDelete()
                },
            )
        }
    }
}
