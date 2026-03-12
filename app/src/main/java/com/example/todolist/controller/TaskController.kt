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
import java.text.SimpleDateFormat
import java.util.*


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

    fun addTask(
        name: String, 
        description: String, 
        deadline: String? = null, 
        periodicity: String? = null,
        periodicityDay: Int? = null,
        imageUri: String? = null
    ) {
        if (name.isBlank()) return
        viewModelScope.launch {
            dao.insertTask(Task(
                name = name, 
                description = description, 
                deadline = deadline, 
                periodicity = periodicity,
                periodicityDay = periodicityDay,
                imageUri = imageUri
            ))
        }
    }


    fun updateTask(task: Task, isToggle: Boolean = false) {
        viewModelScope.launch {
            val hasPeriodicity = !task.periodicity.isNullOrBlank() && task.periodicity != "Aucune"

            // Si on coche une tâche qui a une périodicité, on la reporte à la date suivante
            if (isToggle && task.isCompleted && hasPeriodicity) {
                val nextDeadline = calculateNextDeadline(task.deadline, task.periodicity!!, task.periodicityDay)
                dao.updateTask(task.copy(isCompleted = false, deadline = nextDeadline))
            } else {
                dao.updateTask(task)
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.deleteTask(task)
        }
    }

    private fun calculateNextDeadline(currentDeadline: String?, periodicity: String, periodicityDay: Int?): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        
        if (!currentDeadline.isNullOrBlank()) {
            try {
                val date = sdf.parse(currentDeadline)
                if (date != null) calendar.time = date
            } catch (e: Exception) {}
        }

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        if (calendar.before(today)) {
            calendar.time = Date()
        }

        when (periodicity) {
            "Quotidienne" -> {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
            "Hebdomadaire" -> {
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
                if (periodicityDay != null) {
                    val targetDay = when (periodicityDay) {
                        1 -> Calendar.MONDAY
                        2 -> Calendar.TUESDAY
                        3 -> Calendar.WEDNESDAY
                        4 -> Calendar.THURSDAY
                        5 -> Calendar.FRIDAY
                        6 -> Calendar.SATURDAY
                        7 -> Calendar.SUNDAY
                        else -> calendar.get(Calendar.DAY_OF_WEEK)
                    }
                    calendar.set(Calendar.DAY_OF_WEEK, targetDay)
                }
            }
            "Mensuelle" -> {
                calendar.add(Calendar.MONTH, 1)
                if (periodicityDay != null) {
                    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                    calendar.set(Calendar.DAY_OF_MONTH, minOf(periodicityDay, maxDay))
                }
            }
        }
        return sdf.format(calendar.time)
    }
}
