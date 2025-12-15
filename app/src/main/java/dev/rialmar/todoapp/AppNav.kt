package dev.rialmar.todoapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.rialmar.todoapp.views.Tareas
import dev.rialmar.todoapp.views.login

@Composable
fun NavApp(modo: Boolean){
    val navController = rememberNavController()
    var nombre by remember { mutableStateOf("") }
    var alias by remember { mutableStateOf("") }
    NavHost(navController = navController, startDestination = "login"){
        composable("login") {
            login(
                onCambioNombre = { nombre = it },
                navController = navController,
                onCambioAlias = {alias = it},
                modo = modo
            )
        }
        composable("Tareas") {
            Tareas(
                nombre = nombre,
                alias = alias,
            )
        }
    }
}
