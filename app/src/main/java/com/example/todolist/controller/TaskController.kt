package com.example.todolist.controller

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolist.data.Task
import com.example.todolist.data.TaskDatabase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class TaskController(application: Application) : AndroidViewModel(application) {

    private val dao = TaskDatabase.getDatabase(application).taskDao()

    val tasks: StateFlow<List<Task>> = dao.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Vérification de l'affichage des retards
    var hasCheckedOverdue: Boolean = false

    fun addTask(name: String, description: String, deadline: String? = null) {
        if (name.isBlank()) return
        viewModelScope.launch {
            dao.insertTask(Task(name = name, description = description, deadline = deadline))
        }
    }


    fun updateTask(task: Task) {
        viewModelScope.launch {
            dao.updateTask(task)
        }
    }
}
