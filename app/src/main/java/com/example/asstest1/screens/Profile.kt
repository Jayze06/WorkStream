package com.example.asstest1.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.asstest1.R
import com.example.asstest1.model.UserModel

@Composable
fun Profile(navHostController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val firebaseUser by authViewModel.firebaseUser.observeAsState()
    val userData by authViewModel.userData.observeAsState()

    var showUsernameDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

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
        }
    }

    if (isPortrait) {
        PortraitProfileLayout(
            userData = userData,
            onUpdateProfilePicture = { uri ->
                authViewModel.updateUserProfile(userData?.name ?: "", userData?.bio ?: "", userData?.email ?: "", uri)
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
            onUpdateProfilePicture = { uri ->
                authViewModel.updateUserProfile(userData?.name ?: "", userData?.bio ?: "", userData?.email ?: "", uri)
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
        ProfileButtons(onShowUsernameDialog, onShowPasswordDialog, onLogout)
    }
}

@Composable
fun LandscapeProfileLayout(
    userData: UserModel?,
    onUpdateProfilePicture: (Uri?) -> Unit,
    showUsernameDialog: Boolean,
    showPasswordDialog: Boolean,
    onShowUsernameDialog: () -> Unit,
    onShowPasswordDialog: () -> Unit,
    onLogout: () -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Top
    ) {
        ProfileContent(userData, onUpdateProfilePicture)
        Spacer(modifier = Modifier.width(32.dp))
        ProfileButtons(onShowUsernameDialog, onShowPasswordDialog, onLogout)
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
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            onUpdateProfilePicture(uri)
        }
    }

    Surface(
        modifier = Modifier
            .wrapContentWidth()  // Ensure the content doesn't take up unnecessary space
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

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Password", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                TextField(
                    value = currentPass,
                    onValueChange = { currentPass = it },
                    label = { Text("Current Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
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

/*
@Composable
fun Profile(navHostController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val firebaseUser by authViewModel.firebaseUser.observeAsState()
    val userData by authViewModel.userData.observeAsState()

    var showUsernameDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

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
*/