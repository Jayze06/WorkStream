package com.example.asstest1.model

data class TaskModel(
    val id: String = "",
    val title: String = "",
    val dueDate: Long = 0L, // Store as a timestamp
    val progress: Int = 0, // 0-100 percentage
    val assignedMembers: List<String> = emptyList() // List of user IDs
)
