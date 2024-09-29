package com.example.asstest1.screens

import android.content.res.Configuration
import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.asstest1.model.TaskModel
import com.example.asstest1.viewmodel.TaskViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.asstest1.navigation.Routes
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tasks(navController: NavController) {
    val taskViewModel: TaskViewModel = viewModel()
    val tasks: List<TaskModel> by taskViewModel.tasks.observeAsState(emptyList())

    var showCompletedTasks by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current

    val filteredTasks = if (showCompletedTasks) {
        tasks.filter { it.progress == 100L } // Completed tasks
    } else {
        tasks.filter { it.progress < 100L } // Ongoing tasks
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    // Toggle Button to switch between Completed and Pending Tasks
                    val toggleText = if (showCompletedTasks) "Show Ongoing" else "Show Completed"
                    val backgroundColor = if (showCompletedTasks) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    val contentColor = MaterialTheme.colorScheme.onPrimary

                    Button(
                        onClick = { showCompletedTasks = !showCompletedTasks },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = backgroundColor,
                            contentColor = contentColor
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(0.5f)
                            .height(48.dp)
                    ) {
                        Text(toggleText)
                    }
                },
                actions = {
                    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        IconButton(onClick = { navController.navigate(Routes.AddTask.route) }) {
                            Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add Task")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                FloatingActionButton(onClick = { navController.navigate(Routes.AddTask.route) }) {
                    Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add Task")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            if (filteredTasks.isEmpty()) {
                Text("No tasks available.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredTasks) { task: TaskModel ->
                        TaskItem(task) {
                            // Navigate to task details on click
                            navController.navigate("${Routes.TaskDetail.route}/${task.id}")
                        }
                    }
                }
            }
        }
    }
}