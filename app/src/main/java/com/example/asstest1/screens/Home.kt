package com.example.asstest1.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.asstest1.item_view.ThreadItem
import com.example.asstest1.utils.SharedPref
import com.example.asstest1.viewmodel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Home(navHostController: NavHostController) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel()
    val threadsAndUsers by homeViewModel.threadsAndUser.observeAsState(emptyList()) // Use an empty list as a default

    LazyColumn {
        items(threadsAndUsers) { pairs ->
            // Use safe calls to ensure pairs.first and pairs.second are not null
            ThreadItem(
                thread = pairs.first,
                users = pairs.second,
                navHostController,
                FirebaseAuth.getInstance().currentUser?.uid ?: ""
            )
        }
    }
}