package com.example.asstest1.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.asstest1.model.UserModel
import com.example.asstest1.utils.SharedPref
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class AuthViewModel : ViewModel() {

    val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance()
    val userRef = db.getReference("users")

    private val storageRef = Firebase.storage.reference
    private val imageRef = storageRef.child("users/${UUID.randomUUID()}.jpg")

    private val _firebaseUser = MutableLiveData<FirebaseUser?>()
    val firebaseUser: LiveData<FirebaseUser?> = _firebaseUser

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error


    private val _registrationSuccess = MutableLiveData<Boolean>()
    val registrationSuccess: LiveData<Boolean> = _registrationSuccess

    private val _userData = MutableLiveData<UserModel?>()
    val userData: LiveData<UserModel?> = _userData





    init {

        _firebaseUser.value = auth.currentUser
    }

    fun login(email: String, password: String, context: Context){

        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (it.isSuccessful){
                    _firebaseUser.postValue(auth.currentUser)

                    getData(auth.currentUser!!.uid, context)
                }else{
                    _error.postValue(it.exception!!.message)
                }
            }
    }

    private fun getData(uid: String, context: Context) {
        userRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userData = snapshot.getValue(UserModel::class.java)

                // Use safe call with null check
                if (userData != null) {
                    SharedPref.storeData(
                        userData.name,
                        userData.email,
                        userData.bio,
                        userData.userName,
                        userData.imageUrl, // This can be null
                        context
                    )
                } else {
                    // Handle the case when userData is null
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }



    fun register(
        email: String,
        password: String,
        name: String,
        bio: String,
        userName: String,
        imageUri: Uri?,
        context: Context
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _firebaseUser.postValue(auth.currentUser)

                    // Post value to indicate registration success
                    _registrationSuccess.postValue(true)

                    if (imageUri != null) {
                        saveImage(email, password, name, bio, userName, imageUri, auth.currentUser?.uid, context)
                    } else {
                        saveDataWithoutImage(email, password, name, bio, userName, auth.currentUser?.uid, context)
                    }
                } else {
                    // Post value to indicate registration failure
                    _registrationSuccess.postValue(false)
                    _error.postValue("Something went wrong.")
                }
            }
    }

    private fun saveImage(
        email: String,
        password: String,
        name: String,
        bio: String,
        userName: String,
        imageUri: Uri,
        uid: String?,
        context: Context
    ) {
        val uploadTask = imageRef.putFile(imageUri)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                saveData(email, password, name, bio, userName, downloadUrl.toString(), uid, context) // Pass downloadUrl as nullable String
            }
        }
    }


    private fun saveDataWithoutImage(
        email: String,
        password: String,
        name: String,
        bio: String,
        userName: String,
        uid: String?,
        context: Context
    ) {
        if (uid != null) {
            val userData = UserModel(email, password, name, bio, userName, null, uid)
            userRef.child(uid).setValue(userData)
                .addOnSuccessListener {
                    SharedPref.storeData(name, email, bio, userName, null, context) // Pass null for imageUrl
                }.addOnFailureListener {
                    // Handle the error
                }
        } else {
            // Handle the case when uid is null
        }
    }




    private fun saveData(
        email: String,
        password: String,
        name: String,
        bio: String,
        userName: String,
        imageUrl: String?, // Keep this as nullable
        uid: String?,
        context: Context
    ) {
        if (uid != null) {
            val userData = UserModel(email, password, name, bio, userName, imageUrl, uid)
            userRef.child(uid).setValue(userData)
                .addOnSuccessListener {
                    SharedPref.storeData(name, email, bio, userName, imageUrl, context)
                }.addOnFailureListener {
                    // Handle the error
                }
        } else {
            // Handle the case when uid is null
        }
    }




    fun logout(){
        auth.signOut()
        _firebaseUser.postValue(null)
    }


    fun fetchUserData(uid: String) {
        userRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _userData.postValue(snapshot.getValue(UserModel::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    // Update user profile
    fun updateUserProfile(name: String, bio: String, email: String, imageUri: Uri?) {
        val uid = firebaseUser.value?.uid ?: return
        val userData = UserModel(email, "", name, bio, "", imageUri?.toString(), uid)

        userRef.child(uid).setValue(userData)
            .addOnSuccessListener {
                // Optionally handle success (e.g., show a message)
            }.addOnFailureListener {
                // Handle error
            }
    }

    // Update password
    fun updatePassword(currentPassword: String, newPassword: String) {
        val user = auth.currentUser
        val email = user?.email ?: return

        // Reauthenticate the user before updating password
        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        user.reauthenticate(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user.updatePassword(newPassword)
                        .addOnCompleteListener { updateTask ->
                            if (updateTask.isSuccessful) {
                                // Optionally fetch updated user data after password change
                                fetchUserData(user.uid)
                            } else {
                                _error.postValue("Password update failed")
                            }
                        }
                } else {
                    _error.postValue("Reauthentication failed")
                }
            }
    }


    fun refreshUserData(uid: String) {
        fetchUserData(uid)  // Re-fetch user data after updating profile or password
    }

}


