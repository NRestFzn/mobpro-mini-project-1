package com.nashirrestafauzian0083.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.nashirrestafauzian0083.todolist.ui.navigation.TodoAppNavigation
import com.nashirrestafauzian0083.todolist.ui.theme.TodoListTheme
import com.nashirrestafauzian0083.todolist.util.SettingsDataStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val dataStore = remember(context) { SettingsDataStore(context) }
            val isDark by dataStore.darkThemeFlow.collectAsState(initial = false)

            TodoListTheme(darkTheme = isDark) {
                TodoAppNavigation()
            }
        }
    }
}
