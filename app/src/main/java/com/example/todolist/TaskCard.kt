package com.example.todolist

import android.hardware.lights.Light
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@Composable
fun TaskCard(
    task: Task,
    onClick: () -> Unit,
    onToggleComplete: () -> Unit
) {
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
            
            Text(
                text = task.name,
                modifier = Modifier.weight(1f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = if (task.isCompleted) Color.Gray else Black,
                style = TextStyle(
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp))

            // Petit carré de validation (peut être décoché)
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
