package com.example.asstest1.screens

import android.content.res.Configuration
import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.asstest1.model.TaskModel
import com.example.asstest1.model.UserModel
import com.example.asstest1.viewmodel.TaskViewModel
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(navController: NavController) {
    val taskViewModel: TaskViewModel = viewModel()

    var title by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(0L) }
    var progress by remember { mutableStateOf(0L) } // Progress as Long
    var dateInput by remember { mutableStateOf("") } // New state variable for date input
    val users by taskViewModel.users.observeAsState(emptyList())
    var selectedMembers by remember { mutableStateOf(mutableStateMapOf<String, Long>()) } // Use Long for progress values

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add Task") },
                actions = {
                    IconButton(onClick = {
                        if (title.isNotBlank() && dueDate > 0 && selectedMembers.isNotEmpty()) {
                            val newTask = TaskModel(
                                id = "",
                                title = title,
                                dueDate = dueDate,
                                progress = progress, // Now using Long
                                assignedMembers = selectedMembers // Correct type: Map<String, Long>
                            )
                            taskViewModel.addTask(newTask)
                            navController.popBackStack()
                        }
                    }) {
                        Text("Save")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                TextField(
                    value = dateInput,
                    onValueChange = { newValue ->
                        dateInput = newValue
                        dueDate = try {
                            SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(newValue)?.time ?: 0L
                        } catch (e: Exception) {
                            0L
                        }
                    },
                    label = { Text("Due Date (dd/MM/yyyy)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                TextField(
                    value = progress.toString(),
                    onValueChange = { progress = it.toLongOrNull() ?: 0L }, // Use Long for progress
                    label = { Text("Progress (0-100)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            items(users) { user ->
                Row(modifier = Modifier.padding(8.dp)) {
                    Checkbox(
                        checked = selectedMembers.contains(user.uid),
                        onCheckedChange = { checked ->
                            if (checked) {
                                selectedMembers[user.uid] = 0L // Add member with progress 0L
                            } else {
                                selectedMembers.remove(user.uid)
                            }
                        }
                    )
                    Text(text = user.name)
                }
            }
        }
    }
}
