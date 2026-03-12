package com.example.todolist

import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.*
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
fun DinoExplosionEffect(visible: Boolean, onAnimationEnd: () -> Unit) {
    if (!visible) return
    val dinoCount = 120
    val duration = 2500
    val anim = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        anim.animateTo(
            targetValue = 1f,
            animationSpec = tween(duration, easing = LinearOutSlowInEasing)
        )
        onAnimationEnd()
    }

    val textMeasurer = rememberTextMeasurer()
    val dinoEmojis = listOf("🦖", "🦕")
    val style = TextStyle(fontSize = 35.sp)

    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val progress = anim.value
        val maxRadius = size.minDimension * 1.5f
        
        for (i in 0 until dinoCount) {
            val rand = Random(i.toLong())
            val emoji = dinoEmojis[rand.nextInt(dinoEmojis.size)]
            val angle = (2 * PI * i / dinoCount + rand.nextFloat() * 0.4 - 0.2).toFloat()
            val speed = maxRadius * (0.2f + rand.nextFloat() * 1.3f)
            
            val x = center.x + cos(angle) * speed * progress
            val y = center.y + sin(angle) * speed * progress + (progress * progress * 800f)
            
            val rotation = progress * 720f * (rand.nextFloat() - 0.5f)
            
            rotate(rotation, Offset(x, y)) {
                val textLayoutResult = textMeasurer.measure(
                    text = AnnotatedString(emoji),
                    style = style
                )
                drawText(
                    textLayoutResult = textLayoutResult,
                    topLeft = Offset(x - textLayoutResult.size.width / 2, y - textLayoutResult.size.height / 2),
                    alpha = 1f - (progress * 0.5f)
                )
            }
        }
    }
}

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
    var showDinoExplosion by remember { mutableStateOf(false) }
    var confettiKey by remember { mutableIntStateOf(0) }
    var dinoKey by remember { mutableIntStateOf(0) }

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

    // Calcul de la progression
    val totalTasks = tasks.size
    val completedTasksCount = tasks.count { it.isCompleted }
    val progress = if (totalTasks > 0) completedTasksCount.toFloat() / totalTasks else 0f
    
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = "progress"
    )

    // Déclenchement de l'explosion de dinosaures quand 100% est atteint
    var lastCompletedCount by remember { mutableIntStateOf(completedTasksCount) }
    LaunchedEffect(completedTasksCount, totalTasks) {
        if (completedTasksCount == totalTasks && totalTasks > 0 && completedTasksCount > lastCompletedCount) {
            dinoKey++
            showDinoExplosion = true
        }
        lastCompletedCount = completedTasksCount
    }

    var expanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("Tous") }

    // Grouper les tâches par état, avec priorité au drapeau
    val sortedTasks = remember(tasks, selectedFilter) {
        val filtered = when (selectedFilter) {
            "À faire" -> tasks.filter { getTaskStatus(it) == "À faire" }
            "Réalisé" -> tasks.filter { getTaskStatus(it) == "Réalisé" }
            "En retard" -> tasks.filter { getTaskStatus(it) == "En retard" }
            else -> tasks
        }
        val sorted = when (selectedFilter) {
            "Plus récentes" -> filtered.sortedByDescending { it.id }
            "Plus anciennes" -> filtered.sortedBy { it.id }
            else -> filtered.sortedWith(
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
        sorted
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

            // Barre de progression avec dinosaure et smiley qui suit
            if (totalTasks > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BoxWithConstraints(modifier = Modifier.weight(1f).height(30.dp), contentAlignment = Alignment.CenterStart) {
                        val barWidth = this.maxWidth
                        LinearProgressIndicator(
                            progress = { animatedProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .align(Alignment.Center),
                            color = DarkBackground,
                            trackColor = Color.LightGray.copy(alpha = 0.3f),
                            strokeCap = StrokeCap.Round
                        )
                        Text(
                            text = "🍖",
                            fontSize = 20.sp,
                            modifier = Modifier
                                .offset(x = barWidth * animatedProgress - 12.dp)
                                .align(Alignment.CenterStart)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🦖",
                        fontSize = 32.sp
                    )
                }
                Text(
                    text = "${(progress * 100).toInt()}% complété",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
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
                // Rectangle de filtre à droite du bouton ajouter
                Box {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .height(40.dp)
                            .width(130.dp)
                            .padding(start = 8.dp)
                            .clickable { expanded = true }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(selectedFilter, fontSize = 15.sp, color = Color.Black)
                            Text("▼", color = Color.Black, fontSize = 13.sp)
                        }
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(text = { Text("Tous", color = Color.Black) }, onClick = { selectedFilter = "Tous"; expanded = false })
                        DropdownMenuItem(text = { Text("À faire", color = Color.Black) }, onClick = { selectedFilter = "À faire"; expanded = false })
                        DropdownMenuItem(text = { Text("Réalisé", color = Color.Black) }, onClick = { selectedFilter = "Réalisé"; expanded = false })
                        DropdownMenuItem(text = { Text("En retard", color = Color.Black) }, onClick = { selectedFilter = "En retard"; expanded = false })
                        HorizontalDivider()
                        DropdownMenuItem(text = { Text("Plus récentes", color = Color.Black) }, onClick = { selectedFilter = "Plus récentes"; expanded = false })
                        DropdownMenuItem(text = { Text("Plus anciennes", color = Color.Black) }, onClick = { selectedFilter = "Plus anciennes"; expanded = false })
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 32.dp),
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
                                        // On n'affiche les confettis que si ce n'est pas la dernière tâche
                                        if (completedTasksCount + 1 < totalTasks) {
                                            showConfetti = false
                                            confettiKey++
                                            showConfetti = true
                                        }
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
        key(dinoKey) {
            DinoExplosionEffect(visible = showDinoExplosion) { showDinoExplosion = false }
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
