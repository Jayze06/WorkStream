package com.example.asstest1.screens

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.asstest1.navigation.Routes
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tasks(navController: NavController) {
    val taskViewModel: TaskViewModel = viewModel()
    val tasks: List<TaskModel> by taskViewModel.tasks.observeAsState(emptyList())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tasks") },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.AddTask.route) }) {
                        Icon(imageVector = Icons.Rounded.Add, contentDescription = "Add Task")
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
            if (tasks.isEmpty()) {
                Text("No tasks available.")
            } else {
                LazyColumn {
                    items(tasks) { task: TaskModel ->
                        TaskItem(task) {
                            // Handle task item click, e.g., navigate to task details or edit screen
                            navController.navigate("${Routes.TaskDetail.route}/${task.id}")
                        }
                    }
                }
            }
        }
    }
}

/*@Composable
fun TaskItem(task: TaskModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = task.title, style = MaterialTheme.typography.bodyLarge)

            // Format due date
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(Date(task.dueDate))
            Text(text = "Due: $formattedDate", style = MaterialTheme.typography.bodyMedium)

            Text(text = "Progress: ${task.progress}%", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
*/