package com.example.asstest1.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.asstest1.model.TaskModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskItem(task: TaskModel, onClick: () -> Unit) {
    if (task.title.isEmpty()) return // Prevent rendering if task is invalid

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.title, style = MaterialTheme.typography.bodyLarge)

            // Format due date
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(Date(task.dueDate))
            Text(text = "Due: $formattedDate", style = MaterialTheme.typography.bodyMedium)

            Text(text = "Progress: ${task.progress}%", style = MaterialTheme.typography.bodyMedium)
        }
    }
}