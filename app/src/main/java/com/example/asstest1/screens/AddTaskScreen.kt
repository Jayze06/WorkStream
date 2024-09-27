package com.example.asstest1.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.asstest1.model.TaskModel
import com.example.asstest1.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController) {
    val taskViewModel: TaskViewModel = viewModel()

    var title by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(0L) } // Adjust to your preferred date input
    var progress by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Task") },
                actions = {
                    IconButton(onClick = {
                        if (title.isNotBlank() && dueDate > 0) {
                            val newTask = TaskModel(
                                id = "", // Generate or retrieve ID as needed
                                title = title,
                                dueDate = dueDate,
                                progress = progress
                            )
                            taskViewModel.addTask(newTask)
                            navController.popBackStack() // Navigate back to Tasks
                        }
                    }) {
                        Text("Save")
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
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = dueDate.toString(), // For simplicity, just showing timestamp
                onValueChange = { dueDate = it.toLongOrNull() ?: 0L }, // Convert to Long
                label = { Text("Due Date (timestamp)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = progress.toString(),
                onValueChange = { progress = it.toIntOrNull() ?: 0 },
                label = { Text("Progress (0-100)") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
