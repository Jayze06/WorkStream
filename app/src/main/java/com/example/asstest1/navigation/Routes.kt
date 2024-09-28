package com.example.asstest1.navigation

sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Notification : Routes("notification")
    object Profile : Routes("profile")
    object Search : Routes("search")
    object Splash : Routes("splash")
    object AddThreads : Routes("add_threads")
    object BottomNav : Routes("bottom_nav")
    object Login : Routes("login")
    object Register : Routes("register")
    object Tasks : Routes("tasks")
    object AddTask : Routes("add_task")
    object TaskDetail : Routes("taskDetail/{taskId}") // Correctly include {taskId}
    object UpdateTaskProgress : Routes("update_task_progress/{taskId}")
}


/* //V1
sealed class Routes(val routes:String) {

    object Home : Routes("home")
    object Notification : Routes("notification")
    object Profile : Routes("profile")
    object Search : Routes("search")
    object Splash : Routes("Splash")
    object AddThreads : Routes("add_threads")
    object BottomNav : Routes("bottom_nav")
    object Login : Routes("login")
    object Register : Routes("register")
}*/

/*
sealed class Routes(val routes: String) {
    object Home : Routes("home")
    object Tasks : Routes("tasks")
    object Threads : Routes("threads")
    object Profile : Routes("profile")
    object Splash : Routes("splash")
    object Login : Routes("login")
    object Register : Routes("register")
}*/
//new

