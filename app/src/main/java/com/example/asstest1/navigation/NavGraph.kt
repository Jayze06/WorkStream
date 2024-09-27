package com.example.asstest1.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.asstest1.screens.AddThreads
import com.example.asstest1.screens.BottomNav
import com.example.asstest1.screens.Home
import com.example.asstest1.screens.Login
import com.example.asstest1.screens.Notification
import com.example.asstest1.screens.Profile
import com.example.asstest1.screens.Register
import com.example.asstest1.screens.Search
import com.example.asstest1.screens.Splash

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
}