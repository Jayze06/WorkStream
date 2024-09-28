package com.example.asstest1.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.asstest1.viewmodel.TaskViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateTaskProgress(navController: NavController, taskId: String) {
    val taskViewModel: TaskViewModel = viewModel()
    val tasks by taskViewModel.tasks.observeAsState(emptyList())
    val task = tasks.find { it.id == taskId }

    // Observe userMap from ViewModel
    val userMap by taskViewModel.userMap.observeAsState(emptyMap())

    // State to track individual member progress
    val memberProgress = remember { mutableStateMapOf<String, Int>() }

    LaunchedEffect(task) {
        task?.assignedMembers?.forEach { (memberId, progress) ->
            memberProgress[memberId] = progress.toInt() // Convert Long to Int
        }
    }


    var showDialog by remember { mutableStateOf(false) }
    var selectedMemberId by remember { mutableStateOf("") }
    var isIncreasing by remember { mutableStateOf(true) }

    // Calculate total progress based on memberProgress
    val totalProgress = memberProgress.values.sum().coerceIn(0, 100)
    val progressBarValue = totalProgress / 100f

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Update Task Progress") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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
            // Total Progress Bar
            LinearProgressIndicator(progress = progressBarValue)
            Text(
                text = "Total Progress: $totalProgress%",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // List of Assigned Members with usernames
            task?.assignedMembers?.forEach { (memberId, memberProgress) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Display username or "Loading..."
                    Text(
                        text = "Member: ${userMap[memberId] ?: "Loading..."}",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )

                    // Minus Button
                    IconButton(onClick = {
                        selectedMemberId = memberId
                        isIncreasing = false
                        showDialog = true
                    }) {
                        Text("-", style = MaterialTheme.typography.bodyLarge)
                    }

                    // Plus Button
                    IconButton(onClick = {
                        selectedMemberId = memberId
                        isIncreasing = true
                        showDialog = true
                    }) {
                        Text("+", style = MaterialTheme.typography.bodyLarge)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Individual Member Progress
                    Text(
                        text = "Progress: ${memberProgress ?: 0}%", // Access progress from assignedMembers directly
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }

    // Show the numpad dialog if needed
    if (showDialog) {
        val memberName = userMap[selectedMemberId] ?: "Unknown User"
        showNumpadDialog(
            memberName = memberName,
            memberId = selectedMemberId,
            memberProgress = memberProgress,
            increase = isIncreasing,
            taskId = taskId,
            taskViewModel = taskViewModel,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
fun showNumpadDialog(
    memberName: String,
    memberId: String,
    memberProgress: MutableMap<String, Int>,
    increase: Boolean,
    taskId: String,
    taskViewModel: TaskViewModel,
    onDismiss: () -> Unit
) {
    var inputValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Enter Progress Amount") },
        text = {
            Column {
                Text("Member: $memberName")
                Spacer(modifier = Modifier.height(8.dp))
                BasicTextField(
                    value = inputValue,
                    onValueChange = { newValue ->
                        // Allow only digits
                        if (newValue.all { it.isDigit() }) {
                            inputValue = newValue
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        .padding(8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = inputValue.toIntOrNull()
                    if (amount != null && amount > 0) {
                        val currentProgress = memberProgress[memberId] ?: 0
                        val newProgress = if (increase) {
                            (currentProgress + amount).coerceAtMost(100)
                        } else {
                            (currentProgress - amount).coerceAtLeast(0)
                        }
                        memberProgress[memberId] = newProgress
                        inputValue = "" // Reset input
                        onDismiss() // Close the dialog

                        // Update the ViewModel
                        taskViewModel.updateMemberProgress(taskId, memberId, newProgress)
                    } else {
                        // Optionally, show an error message or ignore invalid input
                    }
                },
                enabled = inputValue.isNotBlank()
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateTaskProgress(navController: NavController, taskId: String) {
    val taskViewModel: TaskViewModel = viewModel()
    val tasks by taskViewModel.tasks.observeAsState(emptyList())
    val task = tasks.find { it.id == taskId }

    // State to track individual member progress
    val memberProgress = remember { mutableStateMapOf<String, Int>() }

    // Initialize member progress if task is not null
    task?.assignedMembers?.forEach { memberId ->
        memberProgress[memberId] = memberProgress[memberId] ?: 0 // Initialize if not present
    }

    var showDialog by remember { mutableStateOf(false) }
    var selectedMemberId by remember { mutableStateOf("") }
    var isIncreasing by remember { mutableStateOf(true) }

    // Calculate total progress
    val totalProgress = task?.progress ?: 0
    val progressBarValue = totalProgress / 100f

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Update Task Progress") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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
            LinearProgressIndicator(progress = progressBarValue)
            Text("Total Progress: $totalProgress%")

            Spacer(modifier = Modifier.height(16.dp))

            task?.assignedMembers?.forEach { memberId ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Member: $memberId")

                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = {
                        selectedMemberId = memberId
                        isIncreasing = true
                        showDialog = true
                    }) {
                        Text("+")
                    }

                    IconButton(onClick = {
                        selectedMemberId = memberId
                        isIncreasing = false
                        showDialog = true
                    }) {
                        Text("-")
                    }

                    Text("Progress: ${memberProgress[memberId] ?: 0}%")
                }
            }
        }
    }

    // Show the numpad dialog if needed
    if (showDialog) {
        showNumpadDialog(selectedMemberId, memberProgress, isIncreasing) {
            showDialog = false
        }
    }
}

@Composable
fun showNumpadDialog(memberId: String, memberProgress: MutableMap<String, Int>, increase: Boolean, onDismiss: () -> Unit) {
    var inputValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Enter Progress Amount") },
        text = {
            BasicTextField(
                value = inputValue,
                onValueChange = { inputValue = it },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = {
                val amount = inputValue.toIntOrNull()
                if (amount != null) {
                    // Update progress based on the amount entered
                    val currentProgress = memberProgress[memberId] ?: 0
                    val newProgress = currentProgress + (if (increase) amount else -amount)

                    // Ensure new progress does not go below 0
                    memberProgress[memberId] = newProgress.coerceAtLeast(0)

                    // You may also want to update the total task progress here if needed.
                    inputValue = "" // Reset input
                    onDismiss() // Close the dialog
                }
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}*/


/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateTaskProgress(navController: NavController, taskId: String) {
    val taskViewModel: TaskViewModel = viewModel()
    val tasks by taskViewModel.tasks.observeAsState(emptyList())
    val task = tasks.find { it.id == taskId }

    // State to track individual member progress
    val memberProgress = remember { mutableStateMapOf<String, Int>() }

    // Initialize member progress
    task?.assignedMembers?.forEach { memberId ->
        memberProgress[memberId] = 0 // Initialize member progress
    }

    // Calculate total progress
    val totalProgress = task?.progress ?: 0
    val progressBarValue = totalProgress / 100f

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Update Task Progress") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
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
            // Display the progress bar
            LinearProgressIndicator(progress = progressBarValue)
            Text("Total Progress: $totalProgress%")

            Spacer(modifier = Modifier.height(16.dp))

            // List members and their progress
            task?.assignedMembers?.forEach { memberId: String ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Member: $memberId")

                    Spacer(modifier = Modifier.weight(1f))

                    // Increase Progress Button
                    IconButton(onClick = {
                        showNumpadDialog(memberId, memberProgress, increase = true)
                    }) {
                        Text("+")
                    }

                    // Decrease Progress Button
                    IconButton(onClick = {
                        showNumpadDialog(memberId, memberProgress, increase = false)
                    }) {
                        Text("-")
                    }

                    Text("Progress: ${memberProgress[memberId]}%")
                }
            }
        }
    }
}

@Composable
fun showNumpadDialog(memberId: String, memberProgress: MutableStateMap<String, Int>, increase: Boolean) {
    var inputValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { /* Handle dismiss */ },
        title = { Text("Enter Progress Amount") },
        text = {
            BasicTextField(
                value = inputValue,
                onValueChange = { inputValue = it },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = {
                val amount = inputValue.toIntOrNull()
                if (amount != null) {
                    // Update progress based on the amount entered
                    val currentProgress = memberProgress[memberId] ?: 0
                    val newProgress = currentProgress + (if (increase) amount else -amount)

                    // Ensure new progress does not go below 0
                    memberProgress[memberId] = newProgress.coerceAtLeast(0)
                    inputValue = "" // Reset input
                }
            }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { /* Handle dismiss */ }) {
                Text("Cancel")
            }
        }
    )
}*/