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
        BottomNavItem("Tasks", Routes.Tasks.route, Icons.Rounded.Menu),
        BottomNavItem("Discussion", Routes.AddDiscussions.route, Icons.Rounded.Share),
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
