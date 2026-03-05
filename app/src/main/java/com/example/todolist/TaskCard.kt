package com.example.todolist


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todolist.data.Task
import com.example.todolist.ui.theme.*

@Composable
fun TaskCard(
    task: Task,
) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MediumGrayCard)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            
            Spacer(modifier = Modifier.width(8.dp))
            
            Text(
                text = task.name,
                modifier = Modifier.weight(1f),
                fontSize = 18.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                color = Color.Black
            )
            

            Spacer(modifier = Modifier.width(8.dp))

        }
    }
}
