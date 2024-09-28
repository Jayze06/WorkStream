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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
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
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.asstest1.R
import com.example.asstest1.navigation.Routes
import com.example.asstest1.utils.SharedPref
import com.example.asstest1.viewmodel.AddThreadViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.core.view.Change

@Composable
fun AddThreads(navHostController: NavHostController) {

    val threadViewModel: AddThreadViewModel = viewModel()
    val isPosted by threadViewModel.isPosted.observeAsState(false)

    val context = LocalContext.current

    var thread by remember{
        mutableStateOf("")
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val permissionToRequest = if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){
        android.Manifest.permission.READ_MEDIA_IMAGES
    }else android.Manifest.permission.READ_EXTERNAL_STORAGE

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

    LaunchedEffect(isPosted) {
        if (isPosted!!) {
            thread = ""
            imageUri = null
            Toast.makeText(context, "Thread added", Toast.LENGTH_SHORT).show()

            navHostController.navigate(Routes.Home.route) {
                popUpTo(Routes.AddThreads.route) {
                    inclusive = true
                }
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        val (crossPic, text, logo,
            userName, editText, attachMedia,
            replyText, button, imageBox) = createRefs()

        Image(painter = painterResource(id = R.drawable.baseline_close_24),
            contentDescription = "close",
            modifier = Modifier
                .constrainAs(crossPic) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
                .clickable {
                    navHostController.navigate(Routes.Home.route){
                        popUpTo(Routes.AddThreads.route) {
                            inclusive = true
                        }
                    }
                })

        Text(
            text = "Add Thread", style = TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp
            ),  modifier = Modifier.constrainAs(text){
                top.linkTo(crossPic.top)
                start.linkTo(crossPic.end, margin = 12.dp)
                bottom.linkTo(crossPic.bottom)
            })

        Image(painter =
            rememberAsyncImagePainter(
            model = SharedPref.getImage(context)),
            contentDescription = "close",
            modifier = Modifier
                .constrainAs(logo) {
                    top.linkTo(text.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                }
                .size(36.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Text(
            text = SharedPref.getUserName(context),
            style = TextStyle(
                fontSize = 20.sp
            ),  modifier = Modifier.constrainAs(userName){
                top.linkTo(logo.top)
                start.linkTo(logo.end, margin = 12.dp)
                bottom.linkTo(logo.bottom)
            })

        BasicTextFiledWithHint(
            hint = "Start a thread....",
            value = thread,
            onValueChange = {thread = it},
            modifier = Modifier
                .constrainAs(editText) {
                    top.linkTo(userName.bottom)
                    start.linkTo(userName.start)
                    end.linkTo(parent.end)
                }
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth())

        if (imageUri == null){
            Image(painter = painterResource(id = R.drawable.baseline_attach_file_24),
                contentDescription = "close",
                modifier = Modifier
                    .constrainAs(attachMedia) {
                        top.linkTo(editText.bottom)
                        start.linkTo(editText.start)
                    }
                    .clickable {
                        val isGranted = ContextCompat.checkSelfPermission(
                            context, permissionToRequest
                        ) == PackageManager.PERMISSION_GRANTED

                        if (isGranted) {
                            launcher.launch("image/*")
                        } else {
                            permissionLauncher.launch(permissionToRequest)
                        }
                    })
        }else{
            Box(modifier = Modifier
                .background(Color.Gray)
                .padding(1.dp)
                .constrainAs(imageBox) {
                    top.linkTo(editText.bottom)
                    start.linkTo(editText.start)
                    end.linkTo(parent.end)
                }
                .height(250.dp)){
                Image(painter =
                rememberAsyncImagePainter(
                    model = imageUri),
                    contentDescription = "close",
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Icon(imageVector = Icons.Default.Close, contentDescription = "Remove Image",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clickable {
                            imageUri = null
                        })
            }
        }

        Text(
            text = "Anyone can reply",
            style = TextStyle(
                fontSize = 20.sp
            ),  modifier = Modifier.constrainAs(replyText){
                start.linkTo(parent.start, margin = 12.dp)
                bottom.linkTo(parent.bottom, margin = 12.dp)
            })

        TextButton(onClick = {
            if (imageUri == null){
                threadViewModel.saveData(thread,
                    FirebaseAuth.getInstance().currentUser!!.uid,"")
            }else{
                threadViewModel.saveImage(thread,
                    FirebaseAuth.getInstance().currentUser!!.uid, imageUri!!)
            }
        },
            modifier = Modifier.constrainAs(button){
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }) {
            Text(
                text = "Post",
                style = TextStyle(fontSize = 20.sp))
        }
    }
}

@Composable
fun BasicTextFiledWithHint(
    hint:String, value:String,
    onValueChange: (String) -> Unit,
    modifier: Modifier
){
    Box(modifier = modifier){
        if(value.isEmpty()){
            Text(text = hint, color = Color.Gray)
        }

        BasicTextField(value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle.Default.copy(color = Color.Black),
            modifier = Modifier.fillMaxWidth())
    }
}

@Preview
@Composable
fun AddPostView(){
    //AddThreads()
}