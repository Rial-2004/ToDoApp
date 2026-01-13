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
import dev.rialmar.todoapp.views.signIn

@Composable
fun NavApp(modo: Boolean){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login"){
        composable("login") {
            login(modo, navController)
        }
        composable("signIn"){
            signIn(modo, navController)
        }
        composable("Tareas") {
            Tareas()
        }
    }
}
