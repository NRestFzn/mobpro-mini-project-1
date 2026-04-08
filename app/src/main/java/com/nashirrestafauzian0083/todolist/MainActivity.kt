package com.nashirrestafauzian0083.todolist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nashirrestafauzian0083.todolist.ui.navigation.TodoAppNavigation
import com.nashirrestafauzian0083.todolist.ui.theme.TodoListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoListTheme {
                TodoAppNavigation()
            }
        }
    }
}
