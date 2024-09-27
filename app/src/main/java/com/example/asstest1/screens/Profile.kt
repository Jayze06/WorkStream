package com.example.asstest1.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.asstest1.viewmodel.AuthViewModel
import com.example.asstest1.navigation.Routes
import androidx.compose.ui.graphics.Color

// Necessary imports for state delegation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation

@Composable
fun Profile(navHostController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val firebaseUser by authViewModel.firebaseUser.observeAsState()
    val userData by authViewModel.userData.observeAsState()

    var showUsernameDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    LaunchedEffect(firebaseUser) {
        if (firebaseUser == null) {
            navHostController.navigate(Routes.Login.routes) {
                popUpTo(navHostController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        } else {
            authViewModel.fetchUserData(firebaseUser?.uid ?: "")
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Profile", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        // Show user info
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Name: ${userData?.name ?: "Loading..."}")
                Text(text = "Email: ${userData?.email ?: "Loading..."}")
                Text(text = "Bio: ${userData?.bio ?: "No bio available"}")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Update Profile Button
        Button(onClick = { showUsernameDialog = true }) {
            Text(text = "Update Profile")
        }

        if (showUsernameDialog) {
            ProfileDialog(
                currentName = userData?.name ?: "",
                currentBio = userData?.bio ?: "",
                currentEmail = userData?.email ?: "",
                onConfirm = { name, bio, email ->
                    authViewModel.updateUserProfile(name, bio, email, null) // Pass imageUri as null for now
                    showUsernameDialog = false
                    authViewModel.fetchUserData(firebaseUser?.uid ?: "") // Refresh data after update
                },
                onDismiss = { showUsernameDialog = false }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Update Password Section
        Button(onClick = { showPasswordDialog = true }) {
            Text(text = "Update Password")
        }

        if (showPasswordDialog) {
            PasswordDialog(
                onConfirm = { oldPass, newPass ->
                    authViewModel.updatePassword(oldPass, newPass)
                    showPasswordDialog = false
                    authViewModel.fetchUserData(firebaseUser?.uid ?: "") // Optionally refresh data
                },
                onDismiss = { showPasswordDialog = false }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logout button
        Button(onClick = { authViewModel.logout() }) {
            Text(text = "Logout")
        }
    }
}




@Composable
fun ProfileDialog(
    currentName: String,
    currentBio: String,
    currentEmail: String,
    onConfirm: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var bio by remember { mutableStateOf(currentBio) }
    var email by remember { mutableStateOf(currentEmail) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Update Profile") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
                TextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") }
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(name, bio, email) }) {
                Text("Update")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}


@Composable
fun PasswordDialog(
    onConfirm: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var currentPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Password") },
        text = {
            Column {
                TextField(
                    value = currentPass,
                    onValueChange = { currentPass = it },
                    label = { Text("Current Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
                TextField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(currentPass, newPass) }) {
                Text("Update Password")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
