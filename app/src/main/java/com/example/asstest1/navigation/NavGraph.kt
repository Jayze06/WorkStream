package com.example.asstest1.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.asstest1.screens.*

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("login") { Login(navController) }
        composable(Routes.Register.route) { Register(navController) }
        composable(Routes.Home.route) { Home(navController) }
        composable(Routes.Splash.route){ Splash(navController) }
        composable(Routes.Tasks.route) { Tasks(navController) }
        composable(Routes.AddDiscussions.route) { AddDiscussions(navController) }
        composable(Routes.Profile.route) { Profile(navController) }
        composable("${Routes.TaskDetail.route}/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            TaskDetail(navController, taskId)
        }
        composable(Routes.UpdateTaskProgress.route) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            UpdateTaskProgress(navController, taskId)
        }
        composable(Routes.AddTask.route) { AddTaskScreen(navController) }
    }
}
