package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.todolist.ui.theme.ToDoListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoListTheme() {
                MaterialTheme {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation(){

    //controleur navigation
    val navController = rememberNavController()
    val name=

        //Définir le systeme de navigation
        NavHost(navController = navController, startDestination = "home"){
            composable("home"){
                HomeScreen(navController = navController)
            }
            composable("form"){
                FormScreen(navController = navController)
            }
            composable(
                "display/{name}/{age}",
                listOf(
                    navArgument("name"){defaultValue=""},
                    navArgument("age"){defaultValue=""}
                )
            ){backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                val age = backStackEntry.arguments?.getString("age") ?: ""
            }

        }
}
@Composable
fun HomeScreen(navController: NavController){
    Column(
        modifier=Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hello World !",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(24.dp)) //oerlet un espace entre les composants
        Button(onClick = { navController.navigate("form") }) { //naviguer vers "secondPage"
            Text(text = "Ajouter une tâche")
        }
    }
}

@Composable
fun FormScreen(navController: NavController){
    var nameTask by remember {mutableStateOf("")}
    var description by remember {mutableStateOf("")}

    Column(
        modifier=Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement=Arrangement.Center,
        horizontalAlignment=Alignment.CenterHorizontally
    ) {
        Button(onClick = { navController.popBackStack() }) {
            Text(text = "<--")
        }
        TextField(
            value = nameTask,
            onValueChange={newText -> nameTask=newText},
            label = {Text("Nom de la tâche")},
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
        TextField(
            value = description,
            onValueChange={newText -> description=newText},
            label = {Text("Entrez la description")},
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
        Button(onClick = {  }
        ) {
            Text(text = "Modifier")
        }
        Button(onClick = { /*navController.navigate("home/$nameTask/$description")*/ }) {
            Text(text = "Enregistrer")
        }
    }
}