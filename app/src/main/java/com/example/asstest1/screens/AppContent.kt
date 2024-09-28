package com.example.asstest1.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.asstest1.navigation.NavGraph
import com.example.asstest1.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent(navController: NavHostController) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry.value?.destination?.route ?: Routes.Home.route

    Scaffold(
        topBar = {
            TopAppBar(
                title = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                onNotificationClick = { /* Handle notification click */ }
            )
        },
        bottomBar = { MyBottomBar(navController) }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            startDestination = Routes.Login.route
        )
    }
}
