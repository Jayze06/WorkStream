package com.example.asstest1.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.ArrowBack
import com.example.asstest1.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    title: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pageTitle = when (title) {
        Routes.Home.route -> "Home"
        Routes.Tasks.route -> "Tasks"
        Routes.AddTask.route -> "Add Task"
        Routes.TaskDetail.route -> "Task Detail"
        Routes.AddThreads.route -> "Thread"
        Routes.UpdateTaskProgress.route -> "Update Task"
        Routes.Profile.route -> "Profile"
        Routes.Notification.route -> "Notifications"
        //Routes.TaskDetail.route -> "Task Details"
        else -> "App"
    }

    CenterAlignedTopAppBar(
        title = { Text(pageTitle, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color(0xFFFFA500), // Customize your color
            titleContentColor = Color.White
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = { navigateUp() }) {
                    Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            IconButton(onClick = { onNotificationClick() }) {
                Icon(imageVector = Icons.Rounded.Notifications, contentDescription = "Pending Tasks")
            }
        }
    )
}