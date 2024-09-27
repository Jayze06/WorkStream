package com.example.asstest1.model

data class TaskModel(
    val id: String = "",
    val title: String = "",
    val dueDate: Long = 0L, // Ensure this is a Long type for timestamps
    val progress: Int = 0,
    val assignedMembers: List<String> = listOf()
)