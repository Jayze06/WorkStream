package com.example.asstest1.navigation

sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Notification : Routes("notification")
    object Profile : Routes("profile")
    object Splash : Routes("Splash")
    object AddDiscussions : Routes("add_discussions")
    object Login : Routes("login")
    object Register : Routes("register")
    object Tasks : Routes("tasks")
    object AddTask : Routes("add_task")
    object TaskDetail : Routes("taskDetail/{taskId}")
    object UpdateTaskProgress : Routes("update_task_progress/{taskId}")
}
