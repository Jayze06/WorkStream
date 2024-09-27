package com.example.asstest1.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.asstest1.model.TaskModel
import com.example.asstest1.model.UserModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class TaskViewModel : ViewModel() {

    // Firebase Realtime Database references
    private val db: FirebaseDatabase = Firebase.database
    private val taskReference = db.getReference("tasks")
    private val userReference = db.getReference("users")

    // LiveData for tasks and users
    private val _tasks = MutableLiveData<List<TaskModel>>()
    val tasks: LiveData<List<TaskModel>> = _tasks

    private val _users = MutableLiveData<List<UserModel>>()
    val users: LiveData<List<UserModel>> = _users



    // Initialize data fetching
    init {
        fetchTasks()
        fetchUsers()
    }

    fun getUserById(memberId: String, onResult: (String?) -> Unit) {
        val userRef = FirebaseFirestore.getInstance().collection("users").document(memberId)
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val username = document.getString("username")
                if (username == null) {
                    Log.d("Firestore", "Username is null for userId: $memberId")
                }
                onResult(username ?: "Unknown User")
            } else {
                Log.d("Firestore", "Document does not exist for userId: $memberId")
                onResult(null)
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting user: $exception")
            onResult(null)
        }
    }


    // Fetch users from Firebase
    private fun fetchUsers() {
        userReference.get().addOnSuccessListener { snapshot ->
            val userList = mutableListOf<UserModel>()
            snapshot.children.forEach { childSnapshot ->
                val user = childSnapshot.getValue(UserModel::class.java)
                user?.let { userList.add(it) }
            }
            _users.value = userList
            Log.d("TaskViewModel", "Fetched users: $userList") // Log fetched users
        }.addOnFailureListener { exception ->
            Log.e("TaskViewModel", "Error fetching users", exception) // Log error
        }
    }

    private fun fetchTasks() {
        taskReference.get().addOnSuccessListener { snapshot ->
            val taskList = mutableListOf<TaskModel>()
            snapshot.children.forEach { childSnapshot ->
                val task = childSnapshot.getValue(TaskModel::class.java)
                task?.let { taskList.add(it) }
            }
            _tasks.value = taskList
            Log.d("TaskViewModel", "Fetched tasks: $taskList") // Log fetched tasks
        }.addOnFailureListener { exception ->
            Log.e("TaskViewModel", "Error fetching tasks", exception) // Log error
        }
    }

    // Add a new task to Firebase
    fun addTask(task: TaskModel) {
        taskReference.push().setValue(task)
    }

    // Update an existing task in Firebase
    fun updateTask(task: TaskModel) {
        taskReference.child(task.id).setValue(task)
    }

    // Delete a task from Firebase
    fun deleteTask(taskId: String) {
        taskReference.child(taskId).removeValue()
    }

    // Function to generate a report (logic to be implemented)
    fun generateReport(taskId: String) {
        // Logic to generate a report based on task progress by each member
    }
}
