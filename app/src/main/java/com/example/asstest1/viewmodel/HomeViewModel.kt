package com.example.asstest1.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.asstest1.model.DiscussionModel
import com.example.asstest1.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance()
    private val discussionRef = db.getReference("discussions")

    private val _discussionsAndUsers = MutableLiveData<List<Pair<DiscussionModel, UserModel>>>()
    val discussionsAndUser: LiveData<List<Pair<DiscussionModel, UserModel>>> = _discussionsAndUsers

    private var discussionsFetchedCount = 0

    init {
        fetchDiscussionsAndUsers()
    }

    fun fetchDiscussionsAndUsers() {
        discussionRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("HomeViewModel", "Discussions snapshot: ${snapshot.value}") // Debugging

                val result = mutableListOf<Pair<DiscussionModel, UserModel>>()
                discussionsFetchedCount = 0 // Reset count for each new snapshot

                for (discussionSnapshot in snapshot.children) {
                    val discussion = discussionSnapshot.getValue(DiscussionModel::class.java)
                    discussion?.let {
                        fetchUserFromDiscussion(it) { user ->
                            result.add(it to user)
                            discussionsFetchedCount++
                            Log.d("HomeViewModel", "Discussion fetched: ${it.discussion}, User: ${user.userName}") // Debugging

                            if (discussionsFetchedCount == snapshot.childrenCount.toInt()) {
                                _discussionsAndUsers.value = result
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeViewModel", "Error fetching discussions: ${error.message}")
            }
        })
    }

    private fun fetchUserFromDiscussion(discussion: DiscussionModel, onResult: (UserModel) -> Unit) {
        db.getReference("users").child(discussion.userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(UserModel::class.java)
                    user?.let {
                        onResult(it)
                        Log.d("HomeViewModel", "User fetched: ${it.userName}") // Debugging
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HomeViewModel", "Error fetching user: ${error.message}")
                }
            })
    }
}

