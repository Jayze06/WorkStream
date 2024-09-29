package com.example.asstest1.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asstest1.model.TaskModel
import com.example.asstest1.model.UserModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TaskViewModel : ViewModel() {

    // Firebase Realtime Database references
    private val db: FirebaseDatabase = Firebase.database
    private val taskReference = db.getReference("tasks")
    private val userReference = db.getReference("users")

    private val _userTaskTitles = MutableLiveData<List<String>>()
    val userTaskTitles: LiveData<List<String>> = _userTaskTitles

    // LiveData for tasks and users
    private val _tasks = MutableLiveData<List<TaskModel>>()
    val tasks: LiveData<List<TaskModel>> = _tasks

    private val _users = MutableLiveData<List<UserModel>>()
    val users: LiveData<List<UserModel>> = _users

    // LiveData for mapping memberId to userName
    private val _userMap = MutableLiveData<Map<String, String>>()
    val userMap: LiveData<Map<String, String>> = _userMap

    // Initialize data fetching
    init {
        fetchTasks()
        fetchUsers()
    }


    fun getUserById(memberId: String, onResult: (String?) -> Unit) {
        val userRef = FirebaseFirestore.getInstance().collection("users").document(memberId)
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val userName = document.getString("userName")  // Ensure this matches your Firestore field name
                onResult(userName ?: "User not found")  // Return "User not found" if userName is null
            } else {
                onResult("User not found")  // Document doesn't exist
            }
        }.addOnFailureListener { exception ->
            Log.e("Firestore", "Error getting user: $exception")
            onResult("User not found")  // Handle error case
        }
    }

//    fun getUserById(memberId: String, onResult: (String?) -> Unit) {
//        val userRef = FirebaseFirestore.getInstance().collection("users").document(memberId)
//        userRef.get().addOnSuccessListener { document ->
//            if (document.exists()) {
//                val userName = document.getString("userName") // Ensure field name matches Firestore
//                onResult(userName ?: "User not found")  // Return "User not found" if userName is null
//            } else {
//                onResult("User not found")  // Document doesn't exist
//            }
//        }.addOnFailureListener { exception ->
//            Log.e("Firestore", "Error getting user: $exception")
//            onResult("User not found")  // Handle error case
//        }
//    }



    fun fetchUserNames(memberIds: List<String>) {
        viewModelScope.launch {
            val currentMap = _userMap.value?.toMutableMap() ?: mutableMapOf()

            // Use Firebase Firestore to fetch usernames asynchronously
            memberIds.forEach { memberId ->
                if (!currentMap.containsKey(memberId)) {
                    getUserById(memberId) { userName ->
                        currentMap[memberId] = userName ?: "User not found"
                        _userMap.value = currentMap.toMap() // Update LiveData once new user is fetched
                    }
                }
            }
        }
    }



//    // In TaskViewModel: Add logging inside fetchUserNames
//    fun fetchUserNames(memberIds: List<String>) {
//        val currentMap = _userMap.value?.toMutableMap() ?: mutableMapOf()
//
//        // Filter out the IDs that we already have in the map
//        val idsToFetch = memberIds.filter { !currentMap.containsKey(it) }
//
//        if (idsToFetch.isEmpty()) return  // No need to fetch if all IDs are already in the map
//
//        idsToFetch.forEach { memberId ->
//            getUserById(memberId) { userName ->
//                currentMap[memberId] = userName ?: "Unknown User"
//                _userMap.value = currentMap.toMap()  // Update LiveData with new user data
//                Log.d("TaskViewModel", "Fetched username for $memberId: ${userName ?: "Unknown User"}")
//            }
//        }
//    }



//    // Function to fetch and update userMap
//    fun fetchUserNames(memberIds: List<String>) {
//        // Initialize or get current userMap
//        val currentMap = _userMap.value?.toMutableMap() ?: mutableMapOf()
//
//        memberIds.forEach { memberId ->
//            // Fetch only if not already present
//            if (!currentMap.containsKey(memberId)) {
//                getUserById(memberId) { userName ->
//                    currentMap[memberId] = userName ?: "Unknown User"
//                    _userMap.value = currentMap.toMap() // Update LiveData
//                }
//            }
//        }
//    }

    // Fetch users from Firebase Realtime Database (if needed elsewhere)
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

    // Fetch tasks from Firebase Realtime Database
    private fun fetchTasks() {
        viewModelScope.launch {
            try {
                val snapshot = taskReference.get().await() // Use suspend function with coroutine
                val taskList = mutableListOf<TaskModel>()
                val memberIds = mutableSetOf<String>()  // Use a set to avoid duplicate member IDs

                snapshot.children.forEach { childSnapshot ->
                    val task = childSnapshot.getValue(TaskModel::class.java)
                    task?.let {
                        taskList.add(it)
                        // Collect all member IDs from assignedMembers
                        memberIds.addAll(it.assignedMembers.keys)
                    }
                }

                _tasks.value = taskList
                Log.d("TaskViewModel", "Fetched tasks: $taskList")

                // Fetch usernames after getting tasks
                if (memberIds.isNotEmpty()) {
                    fetchUserNames(memberIds.toList())
                }
            } catch (exception: Exception) {
                Log.e("TaskViewModel", "Error fetching tasks", exception)
            }
        }
    }


//    // Fetch tasks from Firebase Realtime Database
//    private fun fetchTasks() {
//        viewModelScope.launch {
//            try {
//                val snapshot = taskReference.get().await() // Use suspend function with coroutine
//                val taskList = mutableListOf<TaskModel>()
//                snapshot.children.forEach { childSnapshot ->
//                    val task = childSnapshot.getValue(TaskModel::class.java)
//                    task?.let { taskList.add(it) }
//                }
//                _tasks.value = taskList
//                Log.d("TaskViewModel", "Fetched tasks: $taskList")
//
//                // Fetch usernames after getting tasks
//                val allMemberIds = taskList.flatMap { it.assignedMembers.keys }.distinct()
//                fetchUserNames(allMemberIds)
//            } catch (exception: Exception) {
//                Log.e("TaskViewModel", "Error fetching tasks", exception)
//            }
//        }
//    }


    // Add a new task to Firebase
    fun addTask(task: TaskModel) {
        val newTaskRef = taskReference.push()  // Get a reference to the new task with a unique ID
        val taskWithId = task.copy(id = newTaskRef.key ?: "") // Copy task with the auto-generated ID

        newTaskRef.setValue(taskWithId)
            .addOnSuccessListener {
                Log.d("TaskViewModel", "Task added successfully with ID: ${taskWithId.id}")
                fetchTasks() // Refresh tasks after adding
            }
            .addOnFailureListener { exception ->
                Log.e("TaskViewModel", "Error adding task", exception)
            }
    }

    // Update an existing task in Firebase
    fun updateTask(task: TaskModel) {
        taskReference.child(task.id).setValue(task)
            .addOnSuccessListener {
                Log.d("TaskViewModel", "Task updated successfully")
                fetchTasks() // Refresh tasks after updating
            }
            .addOnFailureListener { exception ->
                Log.e("TaskViewModel", "Error updating task", exception)
            }
    }

    // Delete a task from Firebase
    fun deleteTask(taskId: String) {
        taskReference.child(taskId).removeValue()
            .addOnSuccessListener {
                Log.d("TaskViewModel", "Task deleted successfully")
                fetchTasks() // Refresh tasks after deletion
            }
            .addOnFailureListener { exception ->
                Log.e("TaskViewModel", "Error deleting task", exception)
            }
    }

    // Function to generate a report (logic to be implemented)
    fun generateReport(taskId: String) {
        // Logic to generate a report based on task progress by each member
    }

    // Update individual member progress and recalculate total task progress
    fun updateMemberProgress(taskId: String, memberId: String, newProgress: Int) {
        val taskRef = taskReference.child(taskId)

        // Ensure the new progress is within 0 and 100
        val boundedNewProgress = newProgress.coerceIn(0, 100).toLong()

        // Update the member's progress
        taskRef.child("assignedMembers").child(memberId).setValue(boundedNewProgress)
            .addOnSuccessListener {
                Log.d("TaskViewModel", "Updated progress for memberId: $memberId to $boundedNewProgress")

                // Fetch updated assignedMembers to recalculate total progress
                taskRef.child("assignedMembers").get().addOnSuccessListener { snapshot ->
                    val assignedMembersMap = snapshot.children
                        .mapNotNull { child ->
                            val key = child.key
                            val value = child.getValue(Long::class.java)
                            if (key != null && value != null) Pair(key, value) else null
                        }
                        .toMap()

                    if (assignedMembersMap.isNotEmpty()) {
                        // Calculate the total progress as the sum of all member progresses
                        val totalProgress = assignedMembersMap.values.sum().coerceAtMost(100)

                        // Update total task progress in Firebase
                        taskRef.child("progress").setValue(totalProgress.toLong())
                            .addOnSuccessListener {
                                Log.d("TaskViewModel", "Total progress updated to $totalProgress%")
                                // After updating the task, fetch the latest data to ensure the UI reflects the new values
                                fetchTasks()
                            }
                            .addOnFailureListener { exception ->
                                Log.e("TaskViewModel", "Error updating total progress", exception)
                            }
                    } else {
                        Log.e("TaskViewModel", "No assigned members to calculate total progress.")
                    }
                }.addOnFailureListener { exception ->
                    Log.e("TaskViewModel", "Error fetching assignedMembers for recalculation", exception)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("TaskViewModel", "Error updating member progress", exception)
            }
    }

    fun fetchUserTaskTitles(userId: String) {
        viewModelScope.launch {
            try {
                val snapshot = taskReference.get().await()
                val taskTitles = mutableListOf<String>()
                snapshot.children.forEach {
                    childSnapshot ->
                    val task = childSnapshot.getValue(TaskModel::class.java)
                    if (task != null && userId in task.assignedMembers.keys){
                        taskTitles.add(task.title)
                    }
                }

                _userTaskTitles.value = taskTitles

                Log.d("TaskViewModel","Fetched task titles for user $userId:$taskTitles")
            }catch (exception: Exception){
                Log.e("TaskViewModel","Error fetching tasl titles for user $userId", exception)
            }

        }
    }

}