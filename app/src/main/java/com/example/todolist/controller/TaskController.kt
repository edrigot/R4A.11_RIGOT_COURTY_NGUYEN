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

/**
 * CONTROLLER (MVC)
 * Fait le lien entre le Model (Room) et la View (Composables).
 * Contient toute la logique métier.
 */
class TaskController(application: Application) : AndroidViewModel(application) {

    private val dao = TaskDatabase.getDatabase(application).taskDao()

    // Liste des tâches exposée à la View, mise à jour automatiquement
    val tasks: StateFlow<List<Task>> = dao.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTask(name: String, description: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            dao.insertTask(Task(name = name, description = description))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.deleteTask(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            dao.updateTask(task)
        }
    }
}

