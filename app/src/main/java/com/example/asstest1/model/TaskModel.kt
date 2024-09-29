package com.example.asstest1.model

data class TaskModel(
    val id: String = "",
    val title: String = "",
    val dueDate: Long = 0L,
    val progress: Long = 0L,
    val assignedMembers: Map<String, Long> = emptyMap()
)