package com.example.asstest1.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.asstest1.model.BottomNavItem
import com.example.asstest1.navigation.Routes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Share
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNav(navController: NavHostController) {
    MyBottomBar(navController)
}

@Composable
fun MyBottomBar(navController: NavHostController) {
    val backStackEntry = navController.currentBackStackEntryAsState()

    // Define your bottom navigation items
    val list = listOf(
        BottomNavItem("Home", Routes.Home.route, Icons.Rounded.Home),
        BottomNavItem("Tasks", Routes.Tasks.route, Icons.Rounded.Menu), // Dashboard icon for Tasks
        //BottomNavItem("Threads", Routes.AddThreads.route, Icons.Rounded.Share), // Speech bubble icon for Threads
        BottomNavItem("Discussion", Routes.AddDiscussions.route, Icons.Rounded.Share), // Speech bubble icon for Discussion
        BottomNavItem("Profile", Routes.Profile.route, Icons.Rounded.Person)
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