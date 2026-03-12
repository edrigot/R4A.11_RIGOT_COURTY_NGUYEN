package com.example.todolist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.data.Task
import com.example.todolist.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit
) {
    // Affichage de l'état
    val statusText = remember(task.isCompleted, task.deadline) {
        if (task.isCompleted) {
            "Réalisé" to LightGreen
        } else if (!task.deadline.isNullOrBlank()) {
            try {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val deadlineDate = sdf.parse(task.deadline)
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
                
                if (deadlineDate != null && deadlineDate.before(today)) {
                    "En retard" to Color.Red
                } else {
                    "À faire" to Black
                }
            } catch (e: Exception) {
                "À faire" to Black
            }
        } else {
            "À faire" to Black
        }
    }

    // Libellé de la périodicité
    val periodicityLabel = remember(task.periodicity, task.periodicityDay) {
        when (task.periodicity) {
            "Quotidienne" -> "Tous les jours"
            "Hebdomadaire" -> {
                val days = listOf("lundi", "mardi", "mercredi", "jeudi", "vendredi", "samedi", "dimanche")
                val dayName = task.periodicityDay?.let { days.getOrNull(it - 1) } ?: "lundi"
                "Tous les $dayName"
            }
            "Mensuelle" -> {
                val day = task.periodicityDay ?: 1
                "Tous les $day du mois"
            }
            else -> ""
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !task.isCompleted) { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) MediumGrayCard.copy(alpha = 0.5f) else MediumGrayCard
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (task.isCompleted) Color.Gray else Black,
                    style = TextStyle(
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    )
                )
                
                // Affichage de l'état
                Text(
                    text = statusText.first,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = statusText.second
                )

                // Affichage de la périodicité sous l'état
                if (periodicityLabel.isNotEmpty()) {
                    Text(
                        text = periodicityLabel,
                        fontSize = 13.sp,
                        color = if (task.isCompleted) Color.Gray else Color.DarkGray
                    )
                }
            }
            
            // Bouton supprimer
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer",
                    tint = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Petit carré de validation
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(1.dp, if (task.isCompleted) LightGreen else Black, RoundedCornerShape(4.dp))
                    .background(if (task.isCompleted) LightGreen.copy(alpha = 0.1f) else Color.Transparent)
                    .clickable { onToggleComplete() },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Text("✓", color = LightGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
