package com.example.todolist

import androidx.compose.animation.core.*
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
import androidx.compose.ui.geometry.Offset
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
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
import kotlin.random.Random

@Composable
fun FireworkConfettiEffect(visible: Boolean, onAnimationEnd: () -> Unit) {
    if (!visible) return
    val confettiCount = 500
    val duration = 1000
    val anim = rememberInfiniteTransition(label = "firework_confetti")
    val progress by anim.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "progress"
    )
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(duration.toLong())
        onAnimationEnd()
    }
    val primaryColors = listOf(
        Color.Red, Color.Blue, Color.Green, Color.Yellow, Color.Magenta, Color.Cyan, Color.White
    )
    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val maxRadius = size.minDimension * 0.45f
        for (i in 0 until confettiCount) {
            val rand = Random(i)
            val angle = (2 * PI * i / confettiCount + rand.nextFloat() * 0.8 - 0.4).toFloat()
            val speed = maxRadius * (0.5f + rand.nextFloat() * 0.7f)
            val sway = sin(progress * PI * (1.5 + rand.nextFloat())) * (rand.nextFloat() * 40f)
            val gravity = progress * progress * (rand.nextFloat() * 120f)
            val x = center.x + cos(angle) * speed * progress + sway
            val y = center.y + sin(angle) * speed * progress + gravity
            val color = primaryColors[rand.nextInt(primaryColors.size)]
            val size = Random(i + 3).nextFloat() * 16f + 8f
            val sizeF = size
            drawRect(
                color = color,
                topLeft = Offset((x - sizeF / 2).toFloat(), (y - sizeF / 2)),
                size = androidx.compose.ui.geometry.Size(sizeF, sizeF),
                alpha = 0.85f
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController, controller: TaskController) {
    val tasks by controller.tasks.collectAsState()
    var showOverdueDialog by remember { mutableStateOf(false) }
    var showConfetti by remember { mutableStateOf(false) }
    var confettiKey by remember { mutableStateOf(0) }

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

    // Grouper les tâches par état, avec priorité au drapeau
    val sortedTasks = remember(tasks) {
        tasks.sortedWith(
            compareByDescending<Task> { it.isPriority }
                .thenBy { task ->
                    when (getTaskStatus(task)) {
                        "En retard" -> 0
                        "À faire" -> 1
                        "Réalisé" -> 2
                        else -> 3
                    }
                }
        )
    }

    // Affichage du pop up des retards
    LaunchedEffect(tasks) {
        if (tasks.isNotEmpty() && !controller.hasCheckedOverdue) {
            val overdueTasks = tasks.filter { getTaskStatus(it) == "En retard" }
            if (overdueTasks.isNotEmpty()) {
                showOverdueDialog = true
            }
            controller.hasCheckedOverdue = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    items(sortedTasks, key = { it.id }) { task ->
                        Column {
                            TaskCard(
                                task = task,
                                onClick = { navController.navigate("form/${task.id}") },
                                onDelete = { controller.deleteTask(task) },
                                onTogglePriority = { controller.updateTask(task.copy(isPriority = !task.isPriority)) },
                                onToggleComplete = {
                                    val wasNotCompleted = !task.isCompleted
                                    controller.updateTask(task.copy(isCompleted = !task.isCompleted))
                                    if (wasNotCompleted) {
                                        showConfetti = false
                                        confettiKey++
                                        showConfetti = true
                                    }
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
        key(confettiKey) {
            FireworkConfettiEffect(visible = showConfetti) { showConfetti = false }
        }
        if (showOverdueDialog) {
            AlertDialog(
                onDismissRequest = { showOverdueDialog = false },
                title = { Text(text = "Tâches en retard !", fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        val overdueTasks = tasks.filter { getTaskStatus(it) == "En retard" }
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
    }
}
