package com.example.todolist

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.todolist.controller.TaskController
import com.example.todolist.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FormScreen(navController: NavController, controller: TaskController, taskId: Int? = null) {
    val tasks by controller.tasks.collectAsState()
    val existingTask = tasks.find { it.id == taskId }

    var nameTask by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var periodicity by remember { mutableStateOf("Aucune") }
    var periodicityDay by remember { mutableStateOf<Int?>(null) }
    var imageUri by remember { mutableStateOf<String?>(null) }
    
    val periodicityOptions = listOf("Aucune", "Quotidienne", "Hebdomadaire", "Mensuelle")
    val daysOfWeek = listOf("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche")
    val daysOfMonth = (1..31).toList()

    var expandedPeriodicity by remember { mutableStateOf(false) }
    var expandedDay by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri?.toString()
    }

    // Configuration du DatePickerDialog pour dd/mm/yyyy
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            deadline = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(existingTask) {
        if (existingTask != null) {
            nameTask = existingTask.name
            description = existingTask.description
            deadline = existingTask.deadline ?: ""
            periodicity = existingTask.periodicity ?: "Aucune"
            periodicityDay = existingTask.periodicityDay
            imageUri = existingTask.imageUri
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(32.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Nom de la tâche et Image
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                TextField(
                    value = nameTask,
                    onValueChange = { nameTask = it },
                    placeholder = { 
                        Text(
                            "Nom de la tâche", 
                            fontSize = 32.sp, 
                            fontWeight = FontWeight.Bold, 
                            lineHeight = 36.sp,
                            color = Black.copy(alpha = 0.3f)
                        ) 
                    },
                    textStyle = TextStyle(
                        fontSize = 32.sp, 
                        fontWeight = FontWeight.Bold,
                        color = Black,
                        lineHeight = 36.sp
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Transparent,
                        unfocusedContainerColor = Transparent,
                        focusedIndicatorColor = Transparent,
                        unfocusedIndicatorColor = Transparent,
                        cursorColor = Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Date butoir
                val isPastDue = remember(deadline) {
                    if (deadline.isBlank()) false
                    else {
                        try {
                            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val deadlineDate = sdf.parse(deadline)
                            val today = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }.time
                            deadlineDate != null && deadlineDate.before(today)
                        } catch (e: Exception) { false }
                    }
                }

                Text(
                    text = if (deadline.isEmpty()) "Ajouter une date butoir" else "Date butoir : $deadline",
                    fontSize = 16.sp,
                    color = if (deadline.isEmpty()) Black.copy(alpha = 0.5f) else if (isPastDue) Color.Red else Black,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 8.dp)
                        .clickable { datePickerDialog.show() }
                )
            }

            // Sélecteur d'image
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightGraySurface)
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Image sélectionnée",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AddAPhoto,
                        contentDescription = "Ajouter une photo",
                        tint = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Fond description
        Surface(
            color = LightGraySurface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Description:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    textDecoration = TextDecoration.Underline,
                    color = Black
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { 
                        Text(
                            "Ecrivez ici la description de votre tâche.",
                            color = Black.copy(alpha = 0.2f)
                        ) 
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Transparent,
                        unfocusedContainerColor = Transparent,
                        focusedIndicatorColor = Transparent,
                        unfocusedIndicatorColor = Transparent,
                        cursorColor = Black
                    ),
                    modifier = Modifier.fillMaxSize(),
                    textStyle = TextStyle(fontSize = 16.sp, color = Black)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Périodicité
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Périodicité:", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Black)
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    Surface(
                        color = LightGraySurface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp).clickable { expandedPeriodicity = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = periodicity, color = Black)
                            Text(text = "▼", color = Black, fontSize = 12.sp)
                        }
                    }
                    DropdownMenu(
                        expanded = expandedPeriodicity,
                        onDismissRequest = { expandedPeriodicity = false },
                        modifier = Modifier.background(White)
                    ) {
                        periodicityOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(text = option, color = Black) },
                                onClick = {
                                    periodicity = option
                                    expandedPeriodicity = false
                                    if (option != "Hebdomadaire" && option != "Mensuelle") periodicityDay = null
                                    else if (periodicityDay == null) periodicityDay = 1
                                }
                            )
                        }
                    }
                }
            }

            if (periodicity == "Hebdomadaire" || periodicity == "Mensuelle") {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = if (periodicity == "Hebdomadaire") "Le :" else "Le jour :", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box {
                        Surface(
                            color = LightGraySurface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(50.dp).clickable { expandedDay = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                val displayText = if (periodicity == "Hebdomadaire") daysOfWeek.getOrElse((periodicityDay ?: 1) - 1) { "Lundi" } else periodicityDay?.toString() ?: "1"
                                Text(text = displayText, color = Black)
                                Text(text = "▼", color = Black, fontSize = 12.sp)
                            }
                        }
                        DropdownMenu(
                            expanded = expandedDay,
                            onDismissRequest = { expandedDay = false },
                            modifier = Modifier.background(White).heightIn(max = 200.dp)
                        ) {
                            if (periodicity == "Hebdomadaire") {
                                daysOfWeek.forEachIndexed { index, day ->
                                    DropdownMenuItem(text = { Text(text = day, color = Black) }, onClick = { periodicityDay = index + 1; expandedDay = false })
                                }
                            } else {
                                daysOfMonth.forEach { day ->
                                    DropdownMenuItem(text = { Text(text = day.toString(), color = Black) }, onClick = { periodicityDay = day; expandedDay = false })
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Button(
                onClick = {
                    if (nameTask.isNotBlank()) {
                        if (existingTask != null) {
                            controller.updateTask(existingTask.copy(
                                name = nameTask, description = description, deadline = deadline,
                                periodicity = periodicity, periodicityDay = periodicityDay,
                                imageUri = imageUri
                            ))
                        } else {
                            controller.addTask(nameTask, description, deadline, periodicity, periodicityDay, imageUri)
                        }
                        navController.popBackStack()
                    }
                },
                enabled = nameTask.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = LightGraySurface),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.width(180.dp).height(50.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (existingTask != null) "Modifier" else "Enregistrer",
                    color = Black,
                    fontSize = 22.sp,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
