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

    // Observe userMap from ViewModel
    val userMap by taskViewModel.userMap.observeAsState(emptyMap())

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
                Text("Title: ${it.title}", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(8.dp))

                // Display progress bar and text
                LinearProgressIndicator(progress = it.progress / 100f)
                Text(
                    text = "Progress: ${it.progress}%", // Display overall progress
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // List of members with usernames and their progress
                Text("Assigned Members:", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    it.assignedMembers.forEach { (memberId, memberProgress) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Display username or "User not found"
                            Text(
                                text = "- ${userMap[memberId] ?: "User not found"}",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "$memberProgress% progress",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Update Progress Button
                Button(
                    onClick = {
                        navController.navigate("update_task_progress/$taskId")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Update Task Progress")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Generate Report Button
                Button(
                    onClick = { taskViewModel.generateReport(taskId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Generate Report")
                }
            } ?: run {
                // Handle case when task is null (e.g., loading or not found)
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}