package com.example.todolist

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todolist.controller.TaskController

@Composable
fun AppNavigation(controller: TaskController = viewModel()) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(navController = navController, controller = controller)
        }
        composable("form") {
            FormScreen(navController = navController, controller = controller)
        }
        composable(
            route = "form/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId")
            FormScreen(navController = navController, controller = controller, taskId = taskId)
        }
    }
}
