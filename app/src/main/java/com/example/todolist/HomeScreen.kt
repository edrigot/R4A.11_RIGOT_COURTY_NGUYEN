package com.example.todolist

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.todolist.controller.TaskController
import com.example.todolist.data.Task
import com.example.todolist.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController, controller: TaskController) {
    val tasks by controller.tasks.collectAsState()
    var showOverdueDialog by remember { mutableStateOf(false) }
    var hasCheckedOverdue by remember { mutableStateOf(false) }

    // Fonction pour déterminer l'état d'une tâche
    fun getTaskStatus(task: Task): String {
        if (task.isCompleted) return "Réalisé"
        if (!task.deadline.isNullOrBlank()) {
            try {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val deadlineDate = sdf.parse(task.deadline)
                val today = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
                if (deadlineDate != null && deadlineDate.before(today)) return "En retard"
            } catch (e: Exception) {}
        }
        return "À faire"
    }

    // Grouper les tâches par état
    val groupedTasks = remember(tasks) {
        tasks.groupBy { getTaskStatus(it) }
    }

    val overdueTasks = groupedTasks.getOrDefault("En retard", emptyList())

    // Affichage du pop up des retards
    LaunchedEffect(tasks) {
        if (tasks.isNotEmpty() && !hasCheckedOverdue) {
            if (overdueTasks.isNotEmpty()) {
                showOverdueDialog = true
            }
            hasCheckedOverdue = true
        }
    }

    if (showOverdueDialog) {
        AlertDialog(
            onDismissRequest = { showOverdueDialog = false },
            title = { Text(text = "Tâches en retard !", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    overdueTasks.forEach { task ->
                        Text("• ${task.name}", fontWeight = FontWeight.Medium, color = Color.Red)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showOverdueDialog = false }) {
                    Text("D'accord")
                }
            },
            containerColor = Color.White,
            titleContentColor = Color.Black,
            textContentColor = Color.Black
        )
    }

    val statusOrder = listOf("En retard", "À faire", "Réalisé")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        
        Text(
            text = "Hello World !",
            fontSize = 50.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        
        Spacer(modifier = Modifier.height(28.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.navigate("form") },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Ajouter",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = DarkBackground,
            shape = RoundedCornerShape(16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                statusOrder.forEach { status ->
                    val tasksInStatus = groupedTasks.getOrDefault(status, emptyList())
                    if (tasksInStatus.isNotEmpty()) {
                        stickyHeader {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DarkBackground)
                                    .padding(vertical = 8.dp)
                            )
                        }
                        
                        items(tasksInStatus) { task ->
                            Column {
                                TaskCard(
                                    task = task,
                                    onClick = { navController.navigate("form/${task.id}") },
                                    onToggleComplete = {
                                        controller.updateTask(task.copy(isCompleted = !task.isCompleted))
                                    }
                                )
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    thickness = 1.dp,
                                    color = Color.Black.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
