package com.example.asstest1.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.asstest1.R
import com.example.asstest1.model.UserModel
import com.example.asstest1.navigation.Routes
import com.example.asstest1.viewmodel.AuthViewModel
import com.example.asstest1.viewmodel.TaskViewModel

@Composable
fun Profile(navHostController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val taskViewModel: TaskViewModel = viewModel()
    val firebaseUser by authViewModel.firebaseUser.observeAsState()
    val userData by authViewModel.userData.observeAsState()
    val userTaskTitles by taskViewModel.userTaskTitles.observeAsState(listOf())

    var showUsernameDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation ==
            android.content.res.Configuration.ORIENTATION_PORTRAIT

    // Redirect to login if not logged in
    LaunchedEffect(firebaseUser) {
        if (firebaseUser == null) {
            navHostController.navigate(Routes.Login.route) {
                popUpTo(navHostController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        } else {
            authViewModel.fetchUserData(firebaseUser?.uid ?: "")
            taskViewModel.fetchUserTaskTitles(firebaseUser?.uid?:"")
        }
    }

    if (isPortrait) {
        PortraitProfileLayout(
            userData = userData,
            userTaskTitles = userTaskTitles,
            onUpdateProfilePicture = { uri ->
                authViewModel.updateUserProfile(userData?.name ?: "",
                    userData?.bio ?: "", userData?.email ?: "", uri)
            },
            showUsernameDialog = showUsernameDialog,
            showPasswordDialog = showPasswordDialog,
            onShowUsernameDialog = { showUsernameDialog = true },
            onShowPasswordDialog = { showPasswordDialog = true },
            onLogout = { authViewModel.logout() }
        )
    } else {
        LandscapeProfileLayout(
            userData = userData,
            userTaskTitles = userTaskTitles,
            onUpdateProfilePicture = { uri ->
                authViewModel.updateUserProfile(userData?.name ?: "",
                    userData?.bio ?: "", userData?.email ?: "", uri)
            },
            showUsernameDialog = showUsernameDialog,
            showPasswordDialog = showPasswordDialog,
            onShowUsernameDialog = { showUsernameDialog = true },
            onShowPasswordDialog = { showPasswordDialog = true },
            onLogout = { authViewModel.logout() }
        )
    }

    // Display the Profile dialog when showUsernameDialog is true
    if (showUsernameDialog) {
        ProfileDialog(
            currentName = userData?.name ?: "",
            currentBio = userData?.bio ?: "",
            currentEmail = userData?.email ?: "",
            onConfirm = { name, bio, email ->
                authViewModel.updateUserProfile(name, bio, email, null)  // Handle profile update
                showUsernameDialog = false  // Close dialog after update
            },
            onDismiss = { showUsernameDialog = false }  // Close dialog when dismissed
        )
    }

    // Display the Password dialog when showPasswordDialog is true
    if (showPasswordDialog) {
        PasswordDialog(
            onConfirm = { oldPass, newPass ->
                authViewModel.updatePassword(oldPass, newPass)  // Handle password update
                showPasswordDialog = false  // Close dialog after update
            },
            onDismiss = { showPasswordDialog = false }  // Close dialog when dismissed
        )
    }
}

@Composable
fun PortraitProfileLayout(
    userData: UserModel?,
    userTaskTitles: List<String>,
    onUpdateProfilePicture: (Uri?) -> Unit,
    showUsernameDialog: Boolean,
    showPasswordDialog: Boolean,
    onShowUsernameDialog: () -> Unit,
    onShowPasswordDialog: () -> Unit,
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        ProfileContent(userData, onUpdateProfilePicture)
        Spacer(modifier = Modifier.height(32.dp))
        //Display task titles
        TaskTitlesSection(userTaskTitles)

        Spacer(modifier = Modifier.height(32.dp))
        ProfileButtons(onShowUsernameDialog, onShowPasswordDialog, onLogout)
    }
}

@Composable
fun LandscapeProfileLayout(
    userData: UserModel?,
    userTaskTitles: List<String>,
    onUpdateProfilePicture: (Uri?) -> Unit,
    showUsernameDialog: Boolean,
    showPasswordDialog: Boolean,
    onShowUsernameDialog: () -> Unit,
    onShowPasswordDialog: () -> Unit,
    onLogout: () -> Unit
) {
    // Scroll state for vertical scrolling
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .border(1.dp, Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                ProfileContent(userData, onUpdateProfilePicture)
            }


            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .border(1.dp, Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .padding(16.dp)
                    .heightIn(max = 300.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Assigned Tasks",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )


                LazyColumn(
                    modifier = Modifier.fillMaxHeight(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(userTaskTitles) { taskTitle ->
                        Text(text = taskTitle, modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Actions",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ProfileButtons(onShowUsernameDialog, onShowPasswordDialog, onLogout)
        }
    }
}

@Composable
fun TaskTitlesSection(userTaskTitles: List<String>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Assigned Tasks:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        userTaskTitles.forEach { title ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .border(1.dp, MaterialTheme.colorScheme.primary)
                    .padding(8.dp),
                shape = MaterialTheme.shapes.small,
                color = MaterialTheme.colorScheme.surface
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ProfileContent(
    userData: UserModel?,
    onUpdateProfilePicture: (Uri?) -> Unit
) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Image picker launcher
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            onUpdateProfilePicture(uri)
        }
    }

    Surface(
        modifier = Modifier
            .wrapContentWidth()
            .padding(horizontal = 16.dp),
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .wrapContentWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile picture
            ProfilePicture(
                imageUri = imageUri,
                onClick = { launcher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Name
            Text(
                text = "Name: ${userData?.name ?: "Loading..."}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            // Email
            Text(
                text = "Email: ${userData?.email ?: "Loading..."}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            // Bio
            Text(
                text = "Bio: ${userData?.bio ?: "No bio available"}",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ProfilePicture(
    imageUri: Uri?,
    onClick: () -> Unit
) {
    val painter: Painter = if (imageUri != null) {
        rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUri)
                .build()
        )
    } else {
        painterResource(id = R.drawable.person) // Default image
    }

    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(CircleShape)
            .clickable { onClick() }
    ) {
        Image(
            painter = painter,
            contentDescription = "Profile Picture",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun ProfileButtons(
    onShowUsernameDialog: () -> Unit,
    onShowPasswordDialog: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { onShowUsernameDialog() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(text = "Update Profile", color = Color.White, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onShowPasswordDialog() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) {
            Text(text = "Update Password", color = Color.White, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onLogout() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text(text = "Logout", color = Color.White, fontSize = 18.sp)
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
        title = { Text(text = "Update Profile", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
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
    var errorMessage by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) } // State for showing password

    // Password validation function
    fun isValidPassword(password: String): Boolean {
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasSymbol = password.any { !it.isLetterOrDigit() }
        return password.length >= 6 && hasUpperCase && hasSymbol
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Password", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                TextField(
                    value = currentPass,
                    onValueChange = { currentPass = it },
                    label = { Text("Current Password") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = currentPass.isBlank() // Show error if current password is empty
                )
                if (currentPass.isBlank()) {
                    Text(
                        text = "Current password cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    label = { Text("New Password") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isValidPassword(newPass) // Show error if new password is invalid
                )
                if (!isValidPassword(newPass)) {
                    Text(
                        text = "Password must be at least 6 characters, " +
                                "contain at least one uppercase letter, and one symbol.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Show Password checkbox
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = showPassword,
                        onCheckedChange = { showPassword = it }
                    )
                    Text("Show Password")
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (currentPass.isNotBlank() && isValidPassword(newPass)) {
                    onConfirm(currentPass, newPass) // Handle password update
                    errorMessage = "" // Clear error message
                } else {
                    errorMessage = "Please fix the errors before proceeding"
                }
            }) {
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
