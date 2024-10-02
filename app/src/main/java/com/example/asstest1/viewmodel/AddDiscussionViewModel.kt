package com.example.asstest1.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.asstest1.model.DiscussionModel
import com.example.asstest1.model.UserModel
import com.example.asstest1.utils.SharedPref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class AddDiscussionViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance()
    val userRef = db.getReference("discussions")

    private val storageRef = Firebase.storage.reference
    private val imageRef = storageRef.child("discussions/${UUID.randomUUID()}.jpg")

    private val _isPosted = MutableLiveData<Boolean>()
    val isPosted: LiveData<Boolean> = _isPosted

    fun saveImage(
        discussion: String,
        userId: String,
        imageUri: Uri,
        ) {
        val uploadTask = imageRef.putFile(imageUri)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener {
                saveData(discussion, userId, it.toString())
            }
        }
    }

    fun saveData(
        discussion: String,
        userId: String,
        image: String,
    ){

        val discussionData = DiscussionModel(discussion, image, userId, System.currentTimeMillis().toString())
        userRef.child(userRef.push().key!!).setValue(discussionData)
            .addOnSuccessListener {

                _isPosted.postValue(true)
            }.addOnFailureListener{
                _isPosted.postValue(false)
            }

    }

}