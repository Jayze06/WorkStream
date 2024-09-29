package com.example.asstest1.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.asstest1.navigation.Routes
import com.example.asstest1.viewmodel.AuthViewModel
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun Login(navController: NavHostController) {
    // ViewModel for authentication
    val authViewModel: AuthViewModel = viewModel()
    val firebaseUser by authViewModel.firebaseUser.observeAsState()
    val error by authViewModel.error.observeAsState(null)

    // Navigate to Home if login is successful
    LaunchedEffect(firebaseUser) {
        if (firebaseUser != null) {
            navController.navigate(Routes.Home.route) { // Navigate to "home"
                popUpTo("login") { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    val context = LocalContext.current
    error?.let { errorMessage ->
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    }

    // Form fields for email and password
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            style = TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            )
        )

        Spacer(modifier = Modifier.height(50.dp))

        // Email input field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Password input field with visual transformation
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Login button
        ElevatedButton(
            onClick = {
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Please provide all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    authViewModel.login(email, password, context)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Login",
                style = TextStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                ),
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }

        // Register button for new users
        TextButton(
            onClick = {
                navController.navigate(Routes.Register.route) {
                    popUpTo("login") { inclusive = true }
                    launchSingleTop = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "New User? Create Account",
                style = TextStyle(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginViewPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "Login View Preview")
    }
}
