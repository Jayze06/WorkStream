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
import com.example.asstest1.navigation.Routes


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetail(navController: NavController, taskId: String) {
    val taskViewModel: TaskViewModel = viewModel()
    val tasks by taskViewModel.tasks.observeAsState(emptyList())
    val task = tasks.find { it.id == taskId }

    var progress by remember { mutableStateOf(task?.progress ?: 0) }

    // Create a map to hold memberId to username mapping
    val userMap = remember { mutableStateMapOf<String, String>() }

    // Fetch user data if task is available
    task?.let {
        it.assignedMembers.forEach { memberId ->
            if (!userMap.containsKey(memberId)) {
                // Assuming you have a method to fetch user details
                taskViewModel.getUserById(memberId) { username ->
                    userMap[memberId] = username ?: "Unknown User"
                }
            }
        }
    }

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
                .padding(16.dp)
        ) {
            task?.let {
                Text("Title: ${it.title}")

                // Display progress bar
                LinearProgressIndicator(progress = it.progress / 100f)

                Text("Progress: ${it.progress}%")

                // List of members with usernames
                Text("Assigned Members:")
                it.assignedMembers.forEach { memberId ->
                    Text("- ${userMap[memberId] ?: memberId}") // Show username if available
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Update progress button
                Button(onClick = { navController.navigate("${Routes.UpdateTaskProgress.routes.replace("{taskId}", taskId)}") }) {
                    Text("Update Task Progress")
                }

                // Generate Report button
                Button(onClick = { taskViewModel.generateReport(taskId) }) {
                    Text("Generate Report")
                }
            }
        }
    }
}


