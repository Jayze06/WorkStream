package com.example.asstest1.item_view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.asstest1.R
import com.example.asstest1.model.DiscussionModel
import com.example.asstest1.model.UserModel
import com.example.asstest1.utils.SharedPref
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun DiscussionItem (
    discussion: DiscussionModel,
    users: UserModel,
    navHostController: NavHostController,
    userId: String
){
    val dateTime = remember(discussion.timeStamp) {
        val timestampLong = discussion.timeStamp.toLongOrNull() ?: 0L // Fallback to 0 if conversion fails
        Instant.ofEpochSecond(timestampLong)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("MM-dd HH:mm"))
    }

    // Log the thread and user data for debugging
    Log.d("DiscussionItem", "Discussion: ${discussion.discussion}, User: ${users.userName}")

    Column {
        ConstraintLayout(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
        ){

            val (userImage, userName,
                date, time,
                title, image) = createRefs()

            Image(painter =
            rememberAsyncImagePainter(
                model = users.imageUrl),
                contentDescription = "close",
                modifier = Modifier
                    .constrainAs(userImage) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .size(36.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Text(
                text = users.userName,
                style = TextStyle(
                    fontSize = 20.sp
                ),  modifier = Modifier.constrainAs(userName){
                    top.linkTo(userImage.top)
                    start.linkTo(userImage.end, margin = 12.dp)
                    bottom.linkTo(userImage.bottom)
                })

            Text(
                text = dateTime,
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.DarkGray
                ),
                modifier = Modifier.constrainAs(date) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end, margin = 16.dp)
                }
            )

            Text(
                text = discussion.discussion,
                style = TextStyle(
                    fontSize = 18.sp
                ),  modifier = Modifier.constrainAs(title){
                    top.linkTo(userName.bottom, margin = 8.dp)
                    start.linkTo(userName.start)
                })

            if (discussion.image != ""){
                Card (modifier = Modifier
                    .constrainAs(image){
                        top.linkTo(title.bottom, margin= 8.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }){
                    Image(
                        painter =
                        rememberAsyncImagePainter(model = discussion.image),
                        contentDescription = "close",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )

                }
            }
        }

        HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
    }
}

