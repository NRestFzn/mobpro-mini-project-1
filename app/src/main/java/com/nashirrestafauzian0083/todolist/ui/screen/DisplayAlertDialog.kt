package com.nashirrestafauzian0083.todolist.ui.screen

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nashirrestafauzian0083.todolist.R

@Composable
fun DisplayAlertDialog(
    permanent: Boolean = false,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        text = {
            Text(
                text = stringResource(
                    if (permanent) R.string.delete_message_permanent
                    else R.string.delete_message
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirmation() }) {
                Text(text = stringResource(R.string.delete_button))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text(text = stringResource(R.string.cancel_button))
            }
        },
        onDismissRequest = { onDismissRequest() }
    )
}
