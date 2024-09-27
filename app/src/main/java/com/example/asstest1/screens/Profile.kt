package com.example.asstest1.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.asstest1.navigation.Routes
import com.example.asstest1.viewmodel.AuthViewModel
import com.example.asstest1.model.UserModel
import com.example.asstest1.R
import androidx.compose.ui.draw.clip


@Composable
fun Profile(navHostController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val firebaseUser by authViewModel.firebaseUser.observeAsState()
    val userData by authViewModel.userData.observeAsState()

    LaunchedEffect(firebaseUser) {
        if (firebaseUser == null) {
            navHostController.navigate(Routes.Login.routes) {
                popUpTo(navHostController.graph.startDestinationId) {
                    inclusive = true // Pop up to the start destination, removing it
                }
                launchSingleTop = true
            }
        } else {
            // Fetch user data if firebaseUser is not null
            authViewModel.fetchUserData(firebaseUser?.uid ?: "")
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Profile", style = MaterialTheme.typography.headlineSmall)

        userData?.let { user ->
            // Display user details
            Image(
                painter = rememberImagePainter(
                    data = user.imageUrl ?: R.drawable.person, // Use placeholder if no image URL
                    builder = {
                        placeholder(R.drawable.person) // Placeholder while loading
                        error(R.drawable.person) // Error image if loading fails
                    }
                ),
                contentDescription = "User Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape) // Clip to a circle
                    .background(Color.LightGray) // Optional: Background color
                    .align(Alignment.CenterHorizontally)
            )

            Text(text = "Name: ${user.name}")
            Text(text = "Email: ${user.email}")
            Text(text = "Bio: ${user.bio ?: "No bio available"}")

            // Button to update profile
            Button(onClick = {
                // Trigger update profile function
                // You can pass the updated values here
                // authViewModel.updateUserProfile(user.name, user.bio ?: "", user.email, null)
            }) {
                Text(text = "Update Profile")
            }

            // Logout button
            Button(onClick = {
                authViewModel.logout()
            }) {
                Text(text = "Logout")
            }
        }
    }
}
