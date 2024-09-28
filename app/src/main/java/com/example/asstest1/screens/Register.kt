package com.example.asstest1.screens

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.asstest1.R
import com.example.asstest1.navigation.Routes
import com.example.asstest1.viewmodel.AuthViewModel

@Composable
fun Register(navHostController: NavHostController){

    var email by remember{
        mutableStateOf("")
    }
    var name by remember{
        mutableStateOf("")
    }
    var userName by remember{
        mutableStateOf("")
    }
    var bio by remember{
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val authViewModel : AuthViewModel = viewModel()
    val firebaseUser by authViewModel.firebaseUser.observeAsState(null)
    val registrationSuccess by authViewModel.registrationSuccess.observeAsState(false)


    val permissionToRequest = if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU){
        android.Manifest.permission.READ_MEDIA_IMAGES
    }else android.Manifest.permission.READ_EXTERNAL_STORAGE


    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()) {
        uri: Uri? ->
        imageUri = uri
    }

   val permissionLauncher = rememberLauncherForActivityResult(
       contract = ActivityResultContracts.RequestPermission()){

       isGranted : Boolean ->
       if (isGranted){

       }else{

       }
   }

    LaunchedEffect(firebaseUser) {
        if (firebaseUser!=null){
            navHostController.navigate(Routes.BottomNav.route){
                popUpTo(navHostController.graph.startDestinationId)
                launchSingleTop = true
            }
        }
    }

    LaunchedEffect(registrationSuccess) {
        if (registrationSuccess) {
            // Show Toast in the correct context
            Toast.makeText(context, "Registration Successful! Please log in.", Toast.LENGTH_SHORT).show()
            navHostController.navigate(Routes.Login.route) {
                popUpTo(navHostController.graph.startDestinationId) {
                    inclusive = true // Remove previous screens from the back stack
                }
                launchSingleTop = true
            }
        }
    }

    Column (modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {

        Text(text = "Register here", style = TextStyle(
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp
            )
        )

        Box(modifier = Modifier.height(20.dp))

        Image(
            painter = if (imageUri==null) painterResource(id = R.drawable.person)
            else rememberAsyncImagePainter(model = imageUri),
            contentDescription = "person",
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .clickable {
                    val isGranted = ContextCompat.checkSelfPermission(
                        context, permissionToRequest
                    ) == PackageManager.PERMISSION_GRANTED

                    if (isGranted) {
                        launcher.launch("image/*")
                    } else {
                        permissionLauncher.launch(permissionToRequest)
                    }
                },
            contentScale = ContentScale.Crop)

        Box(modifier = Modifier.height(50.dp))

        OutlinedTextField(value = email,
            onValueChange = {email=it},
            label = { Text(text = "Email") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ), singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(value = name,
            onValueChange = {name=it},
            label = { Text(text = "Name") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text
            ), singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(value = bio,
            onValueChange = {bio=it},
            label = { Text(text = "Bio") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ), singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(value = userName,
            onValueChange = {userName=it},
            label = { Text(text = "Username") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ), singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )


        OutlinedTextField(value = password,
            onValueChange = {password=it},
            label = { Text(text = "Password") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ), singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Box(modifier = Modifier.height(30.dp))

        ElevatedButton(onClick = {
            if (name.isEmpty() || email.isEmpty() || bio.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Please fill all details", Toast.LENGTH_SHORT).show()
            } else {
                // Pass null to the register function if imageUri is null
                authViewModel.register(email, password, name, bio, userName, imageUri, context)
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Register Here", style = TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            ), modifier = Modifier.padding(vertical = 6.dp))
        }


        TextButton(onClick = {
            navHostController.navigate(Routes.Login.route){
                popUpTo(navHostController.graph.startDestinationId)
                launchSingleTop = true }
                             }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "Already Register? Login Here",style = TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
            )

        }
    }
}



@Preview(showBackground = true)
@Composable
fun RegisterView(){
    //Register()
}