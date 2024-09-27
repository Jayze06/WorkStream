package com.example.asstest1.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.asstest1.model.TaskModel
import com.example.asstest1.viewmodel.TaskViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetail(navController: NavController, taskId: String) {
    val taskViewModel: TaskViewModel = viewModel()

    // Observe the list of tasks and find the specific task by its ID
    val tasks by taskViewModel.tasks.observeAsState(emptyList())
    val task = tasks.find { it.id == taskId }

    // State variables to hold task details for editing
    var title by remember { mutableStateOf(task?.title ?: "") }
    var dueDate by remember { mutableStateOf(task?.dueDate ?: 0L) }
    var progress by remember { mutableStateOf(task?.progress ?: 0) }

    // UI
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Task Detail") },
                actions = {
                    IconButton(onClick = {
                        task?.let { taskViewModel.deleteTask(it.id) }
                        navController.popBackStack()
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Task")
                    }
                }
            )
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
            if (task != null) {
                // Task Title
                Text(text = "Title", style = MaterialTheme.typography.bodyLarge)
                BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth()
                )

                // Task Due Date
                Text(text = "Due Date (timestamp):", style = MaterialTheme.typography.bodyMedium)
                BasicTextField(
                    value = dueDate.toString(),
                    onValueChange = { dueDate = it.toLongOrNull() ?: 0L },
                    modifier = Modifier.fillMaxWidth()
                )

                // Task Progress
                Text(text = "Progress:", style = MaterialTheme.typography.bodyMedium)
                BasicTextField(
                    value = progress.toString(),
                    onValueChange = { progress = it.toIntOrNull() ?: 0 },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    // Update the task with new values
                    taskViewModel.updateTask(task.copy(title = title, dueDate = dueDate, progress = progress))
                    navController.popBackStack()
                }) {
                    Text("Save Changes")
                }
            } else {
                Text("Task not found.")
            }
        }
    }
}
