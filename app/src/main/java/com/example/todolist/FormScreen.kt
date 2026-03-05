package com.example.todolist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.todolist.controller.TaskController
import com.example.todolist.ui.theme.*
import com.example.todolist.data.Task

@Composable
fun FormScreen(navController: NavController, controller: TaskController, taskId: Int? = null) {
    val tasks by controller.tasks.collectAsState()
    val existingTask = tasks.find { it.id == taskId }

    var nameTask by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }


    LaunchedEffect(existingTask) {
        if (existingTask != null) {
            nameTask = existingTask.name
            description = existingTask.description
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
            .padding(32.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // nom de la tâche
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

        Spacer(modifier = Modifier.height(60.dp))

        // fond description
        Surface(
            color = LightGraySurface,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
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

        Spacer(modifier = Modifier.weight(1f))

        // enregistrer ou modifier
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    if (nameTask.isNotBlank()) {
                        if (existingTask != null) {
                            controller.updateTask(existingTask.copy(name = nameTask, description = description))
                        } else {
                            controller.addTask(nameTask, description)
                        }
                        navController.popBackStack()
                    }
                },
                enabled = nameTask.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightGraySurface,
                    disabledContainerColor = LightGraySurface.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(0.dp),
                modifier = Modifier.width(160.dp).height(50.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = if (existingTask != null) "Modifier" else "Enregistrer",
                    color = Black, 
                    fontSize = 24.sp
                )
            }
        }
    }
}
