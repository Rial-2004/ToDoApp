package dev.rialmar.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.tooling.preview.Preview
import dev.rialmar.todoapp.data.SettingsRepository
import dev.rialmar.todoapp.ui.theme.ToDoAppTheme

import dev.rialmar.todoapp.views.Tareas


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val repo = remember { SettingsRepository(context) }
            val modoOscuro by repo.obtenerModoOscuro().collectAsState(initial = false)
            ToDoAppTheme(
                darkTheme = modoOscuro
            ) {
                NavApp(modo = modoOscuro)
            }
        }
    }
}
@Preview(showBackground = true,
    device = "spec:width=1087px,height=2392px,dpi=440,navigation=buttons",
    name = "Nothing Phone 3a", showSystemUi = true
)
@Composable
fun GreetingPreview() {
    val context = LocalContext.current
    val repo = remember { SettingsRepository(context) }
    val modoOscuro by repo.obtenerModoOscuro().collectAsState(initial = false)
    ToDoAppTheme(
        darkTheme = true
    ) {
        NavApp(modo = true)
    }
}