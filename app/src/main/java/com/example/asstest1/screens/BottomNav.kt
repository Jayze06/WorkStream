package com.example.asstest1.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.asstest1.model.BottomNavItem
import com.example.asstest1.navigation.Routes
import java.lang.reflect.Modifier
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Share

/*  //V1
@Composable
fun BottomNav(navController: NavHostController){

    val navController1 = rememberNavController()

    Scaffold(bottomBar= { MyBottomBar(navController1) }) { innerPadding ->
        NavHost(navController = navController1, startDestination = Routes.Home.routes,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)){
            composable(route = Routes.Home.routes){
                Home(navController)
            }

            composable(Routes.Notification.routes){
                Notification()
            }

            composable(Routes.AddThreads.routes){
                AddThreads(navController1)
            }

            composable(Routes.Profile.routes){
                Profile(navController)
            }

            composable(Routes.Search.routes) {
                Search()
            }
        }

    }

}

@Composable
fun MyBottomBar(navController1: NavHostController) {

    val backStackEntry = navController1.currentBackStackEntryAsState()

    val list = listOf(
        BottomNavItem(
            "Home",
            Routes.Home.routes,
            Icons.Rounded.Home
        ),
        BottomNavItem(
            "Search",
            Routes.Search.routes,
            Icons.Rounded.Search
        ),
        BottomNavItem(
            "Add Threads",
            Routes.AddThreads.routes,
            Icons.Rounded.Add
        ),
        BottomNavItem(
            "Notification",
            Routes.Notification.routes,
            Icons.Rounded.Notifications
        ),
        BottomNavItem(
            "Profile",
            Routes.Profile.routes,
            Icons.Rounded.Person
        )
    )

    BottomAppBar {

        list.forEach {

            val selected = it.route ==backStackEntry?.value?.destination?.route

            NavigationBarItem(selected = selected, onClick = {
                navController1.navigate(it.route){
                    popUpTo(navController1.graph.findStartDestination().id){
                        saveState = true
                    }
                    launchSingleTop = true
                }
            },icon = {
                Icon(imageVector = it.icon, contentDescription = it.title)
            })
        }
    }
}

*/

//V2



@Composable
fun BottomNav(navController: NavHostController) {

    val navController1 = rememberNavController()

    Scaffold(bottomBar = { MyBottomBar(navController) }) { innerPadding ->
        NavHost(navController = navController1, startDestination = Routes.Home.routes,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)) {
            composable(Routes.Home.routes) { Home(navController) }
            composable(Routes.Tasks.routes) { Tasks(navController) }
            composable(Routes.AddThreads.routes) { AddThreads(navController) }
            composable(Routes.Profile.routes) { Profile(navController) }
        }
    }
}



@Composable
fun MyBottomBar(navController: NavHostController) {
    val backStackEntry = navController.currentBackStackEntryAsState()

    // Updated list with Dashboard for Tasks and ChatBubble for Threads
    val list = listOf(
        BottomNavItem("Home", Routes.Home.routes, Icons.Rounded.Home),
        BottomNavItem("Tasks", Routes.Tasks.routes, Icons.Rounded.Menu), // Dashboard icon for Tasks
        BottomNavItem("Threads", Routes.AddThreads.routes, Icons.Rounded.Share), // Speech bubble icon for Threads
        BottomNavItem("Profile", Routes.Profile.routes, Icons.Rounded.Person)
    )

    BottomAppBar {
        list.forEach { item ->
            val selected = item.route == backStackEntry.value?.destination?.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                }
            )
        }
    }
}
