package com.example.asstest1.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    val users by taskViewModel.users.observeAsState(emptyList())

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
        if (task == null || users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
                Text(text = "Loading assigned members...")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Title: ${task.title}", style = MaterialTheme.typography.titleLarge)
                }

                item {
                    LinearProgressIndicator(progress = task.progress / 100f)
                    Text(
                        text = "Progress: ${task.progress}%", // Display overall progress
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                item {
                    Text("Assigned Members:", style = MaterialTheme.typography.titleMedium)
                }

                items(task.assignedMembers.toList()) { (memberId, memberProgress) ->
                    val user = users.find { it.uid == memberId }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "- ${user?.name ?: "User not found"}",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "$memberProgress% progress",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            navController.navigate("update_task_progress/$taskId")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Update Task Progress")
                    }
                }

                item {
                    Button(
                        onClick = { taskViewModel.generateReport(taskId) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Generate Report")
                    }
                }
            }
        }
    }
}