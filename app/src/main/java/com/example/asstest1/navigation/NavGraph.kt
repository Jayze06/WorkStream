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
        composable(Routes.Register.routes) { Register(navController) }
        composable(Routes.Home.routes) { Home(navController) }
        composable(Routes.Tasks.routes) { Tasks(navController) }
        composable(Routes.AddThreads.routes) { AddThreads(navController) }
        composable(Routes.Profile.routes) { Profile(navController) }
        composable(Routes.TaskDetail.routes + "/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            TaskDetail(navController, taskId)
        }
        composable(Routes.UpdateTaskProgress.routes) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            UpdateTaskProgress(navController, taskId)
        }
        composable("add_task") { AddTaskScreen(navController) }
    }
}



/*//3.0
@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home.routes,
        modifier = modifier
    ) {
        composable(Routes.Home.routes) { Home(navController) }
        composable(Routes.Tasks.routes) { Tasks(navController) }
        composable(Routes.AddThreads.routes) { AddThreads(navController) }
        composable(Routes.Profile.routes) { Profile(navController) }
    }
}*/


/* //V1
@Composable
fun NavGraph(navController: NavHostController){

    NavHost(navController = navController, startDestination = Routes.Splash.routes) {

        composable(Routes.Splash.routes){
            Splash(navController)
        }

        composable(Routes.Home.routes){
            Home(navController)
        }

        composable(Routes.Notification.routes){
            Notification()
        }

        composable(Routes.AddThreads.routes){
            AddThreads(navController)
        }

        composable(Routes.Profile.routes){
            Profile(navController)
        }

        composable(Routes.Search.routes) {
            Search()
        }

        composable(Routes.BottomNav.routes) {
            BottomNav(navController)
        }

        composable(Routes.Login.routes){
            Login(navController)
        }

        composable(Routes.Register.routes){
            Register(navController)
        }
    }
}*/

/*
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Splash.routes) {
        composable(Routes.Splash.routes) {
            Splash(navController)
        }
        composable(Routes.Home.routes) {
            Home(navController)
        }
        composable(Routes.Tasks.routes) {
            Tasks(navController) // Make sure to create a Tasks Composable
        }
        composable(Routes.Threads.routes) {
            Threads(navController) // Make sure to create a Threads Composable
        }
        composable(Routes.Profile.routes) {
            Profile(navController)
        }
        composable(Routes.Login.routes) {
            Login(navController)
        }
        composable(Routes.Register.routes) {
            Register(navController)
        }
    }
}
*/

/*
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.Splash.routes) {
        composable(Routes.Splash.routes) { Splash(navController) }
        composable(Routes.Home.routes) { Home(navController) }
        composable(Routes.Notification.routes) { Notification() }
        composable(Routes.AddThreads.routes) { AddThreads(navController) }
        composable(Routes.Profile.routes) { Profile(navController) }
        composable(Routes.Search.routes) { Search() }
        composable(Routes.BottomNav.routes) { BottomNav(navController) }
        composable(Routes.Login.routes) { Login(navController) }
        composable(Routes.Register.routes) { Register(navController) }
        composable(Routes.Tasks.routes) { Tasks(navController) }
        composable(Routes.AddTask.routes) { AddTaskScreen(navController) }
        composable(Routes.TaskDetail.routes + "/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
            TaskDetail(navController, taskId)
        }
    }
}
*/