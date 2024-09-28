package com.example.asstest1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.asstest1.navigation.NavGraph
import com.example.asstest1.navigation.Routes
import com.example.asstest1.screens.AppContent
import com.example.asstest1.screens.MyBottomBar
import com.example.asstest1.screens.TopAppBar
import com.example.asstest1.ui.theme.AssTest1Theme
import com.example.asstest1.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AssTest1Theme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val firebaseUser by authViewModel.firebaseUser.observeAsState()

                val startDestination = if (firebaseUser != null) Routes.Home.route else Routes.Login.route

                AppContent(navController)
            }
        }
    }
}