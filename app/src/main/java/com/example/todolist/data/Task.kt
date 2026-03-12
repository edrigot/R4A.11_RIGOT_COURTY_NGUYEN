package com.example.todolist.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val isCompleted: Boolean = false,
    val deadline: String? = null,
    val periodicity: String? = null, // "Aucune", "Quotidienne", "Hebdomadaire", "Mensuelle"
    val periodicityDay: Int? = null // Jour de la semaine (1-7) ou jour du mois (1-31)
)
