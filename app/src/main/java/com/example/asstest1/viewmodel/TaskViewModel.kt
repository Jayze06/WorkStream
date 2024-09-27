package com.example.asstest1.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.asstest1.model.TaskModel
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class TaskViewModel : ViewModel() {

    private val db = Firebase.database
    private val taskReference = db.getReference("tasks")

    private val _tasks = MutableLiveData<List<TaskModel>>()
    val tasks: LiveData<List<TaskModel>> = _tasks

    init {
        fetchTasks()
    }

    private fun fetchTasks() {
        taskReference.get().addOnSuccessListener { snapshot ->
            val taskList = mutableListOf<TaskModel>()
            snapshot.children.forEach { childSnapshot ->
                val task = childSnapshot.getValue(TaskModel::class.java)
                task?.let { taskList.add(it) }
            }
            _tasks.value = taskList
        }
    }

    fun addTask(task: TaskModel) {
        taskReference.push().setValue(task)
    }

    fun updateTask(task: TaskModel) {
        taskReference.child(task.id).setValue(task)
    }

    fun deleteTask(taskId: String) {
        taskReference.child(taskId).removeValue()
    }
}
