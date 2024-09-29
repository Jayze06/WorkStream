package com.example.asstest1.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.asstest1.item_view.DiscussionItem
import com.example.asstest1.model.TaskModel
import com.example.asstest1.navigation.Routes
import com.example.asstest1.utils.SharedPref
import com.example.asstest1.viewmodel.HomeViewModel
import com.example.asstest1.viewmodel.TaskViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Home(navHostController: NavHostController) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel()
    val discussionsAndUsers by homeViewModel.discussionsAndUser.observeAsState(initial = emptyList())

    val taskViewModel: TaskViewModel = viewModel()
    val tasks: List<TaskModel> by taskViewModel.tasks.observeAsState(emptyList())

    var showCompletedTasks by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current

    val filteredTasks = if (showCompletedTasks) {
        tasks.filter { it.progress == 100L } // Completed tasks
    } else {
        tasks.filter { it.progress < 100L } // Ongoing tasks
    }


    // Initialize dataFetched to false
    var dataFetched by remember { mutableStateOf(false) }

    // Use LaunchedEffect to update dataFetched after data is fetched
    LaunchedEffect(key1 = Unit) {
        // Fetch data in the LaunchedEffect block
        homeViewModel.fetchDiscussionsAndUsers()

        // Update dataFetched to true after data is fetched
        dataFetched = true
    }

    // Display a loading indicator and message while data is being fetched
    if (!dataFetched  || filteredTasks.isEmpty()) { // Check if data has been fetched
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
            Text(text = "Loading discussions...")
        }
    } else {
        LazyColumn {
            items(filteredTasks) { task: TaskModel ->
                TaskItem(task) {
                    // Navigate to task details on click
                    navHostController.navigate("${Routes.TaskDetail.route}/${task.id}")
                }
            }
            items(discussionsAndUsers) { (discussion, user) ->
                DiscussionItem(
                    discussion = discussion,
                    users = user,
                    navHostController = navHostController,
                    userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                )
            }

        }
    }
}


