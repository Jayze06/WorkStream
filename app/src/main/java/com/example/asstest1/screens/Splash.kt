package com.example.asstest1.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.asstest1.R
import com.example.asstest1.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.*
import kotlinx.coroutines.delay

@Composable
fun Splash(navController: NavHostController) {

    // Define a ConstraintLayout with full screen size
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        // Create references for the UI components in the layout
        val (image) = createRefs()

        // Define an Image element with constraints for centering
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "logo",
            modifier = Modifier.constrainAs(image) {
                // Link to the parent layout's top, bottom, start, and end for centering
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }.size(500.dp)
        )
    }

    // Navigate to another screen after a delay
    LaunchedEffect(Unit) {
        delay(3000)

        if (FirebaseAuth.getInstance().currentUser!= null)
        navController.navigate(Routes.BottomNav.route){
            popUpTo(navController.graph.startDestinationId)
            launchSingleTop = true
        }
        else
            navController.navigate(Routes.Login.route){
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
    }
}