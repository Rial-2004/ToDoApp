package dev.rialmar.todoapp.views


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DisplayMode

import androidx.compose.material3.Divider

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton

import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.rialmar.todoapp.data.SettingsRepository
import dev.rialmar.todoapp.data.Tarea

import kotlinx.coroutines.launch

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun dialogoNuevaTarea(
    onCerrar: () -> Unit,
    onConfirm: (Tarea)-> Unit
){
    var mostrarSelector by remember { mutableStateOf(false) }

    var auxtitulo by remember { mutableStateOf("") }
    var auxdescripcion by remember { mutableStateOf("") }
    val formatoFecha = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    var fechaSeleccionada by remember {
        mutableStateOf(formatoFecha.format(Date()))
    }

    AlertDialog(
        onDismissRequest = onCerrar,
        text = {
            Column{
                campoTexto(
                    contenido = "Titulo",
                    texto = auxtitulo,
                    onTextoCambiado = { auxtitulo = it })
                campoTexto(
                    contenido = "Descripcion",
                    texto = auxdescripcion,
                    onTextoCambiado = { auxdescripcion = it })

                Divider(modifier = Modifier.padding(top = 15.dp))
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center){
                    TextButton(onClick = {mostrarSelector = true}) {
                        Text(fechaSeleccionada)
                    }
                    IconButton(onClick = {mostrarSelector = true},
                    ) {
                        Icon(Icons.Default.DateRange, contentDescription = null)
                    }
                }


            }
        },
        confirmButton = {
            Button(onClick = {
                if (!auxtitulo.isBlank()) {
                    onConfirm(Tarea(titulo = auxtitulo.trim(), descripcion = auxdescripcion.trim(), fecha = fechaSeleccionada.trim()))
                    onCerrar()
                }
            }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = {
                onCerrar()
            }) {
                Text("Cancel")
            }
        },
        modifier = Modifier.width(300.dp)
    )
    if (mostrarSelector){
        selectorFecha(
            onConfirm = { fecha -> fechaSeleccionada = fecha },
            onCerrar = { mostrarSelector = false },
            formatoFecha = formatoFecha
        )
    }
}

@Composable
fun campoTexto(contenido: String, texto: String, onTextoCambiado: (String) -> Unit){
    OutlinedTextField(
        value = texto,
        onValueChange = onTextoCambiado,
        label = {Text(contenido)},
        singleLine = contenido == "Titulo"
    )
}
@Composable
fun tareaCompleta(tarea: Tarea, onCerrar: () -> Unit){
    AlertDialog(
        onDismissRequest = onCerrar,
        title = {
            Column{
                Text(tarea.titulo, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(tarea.fecha, fontSize = 14.sp)
            }},
        text = {
            Text(tarea.descripcion)},
        confirmButton = {
            Button(onClick = {onCerrar()}) {
                Text("Ok")
            }
        },
    )
}


@Composable
fun dialogoPreferencias(onCerrar: () -> Unit){

    val context = LocalContext.current
    val repo = remember { SettingsRepository(context) }
    val scope = rememberCoroutineScope()

    val opcionesColores = mapOf(
        Color.Red to "Red",
        Color.Blue to "Blue",
        Color.Green to "Green",
        Color.White to "Default"
    )
    val colorActual by repo.obtenerColor().collectAsState(initial = Color.White)
    val modoOscuro by repo.obtenerModoOscuro().collectAsState(initial = false)

    AlertDialog(
        onDismissRequest = onCerrar,
        title = {Text("Preferencias", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())},
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()){
                opcionesColores.forEach { (color, nombre) ->
                    Row(
                        modifier = Modifier.padding(start = 30.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (colorActual == color),
                            onClick = {
                                scope.launch {
                                    repo.guardarColor(color)
                                }
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = color,
                                unselectedColor = color
                            )
                        )
                        Text(nombre, fontWeight = FontWeight.Bold)
                    }
                }
                Divider()
                Text("Modo oscuro", modifier = Modifier.padding(start = 35.dp, top = 10.dp))
                    Switch(
                        checked = modoOscuro,
                        onCheckedChange = {modo ->
                            scope.launch {
                             repo.guardarModoOscuro(modo)
                            }},
                        modifier = Modifier.padding(start = 50.dp)
                    )
            }
        },
        confirmButton = {
            IconButton(onClick = onCerrar) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        modifier = Modifier.width(200.dp)
    )
}

@Composable
fun dialogoEliminar(onCerrar: () -> Unit, onConfirmar: () -> Unit){
    AlertDialog(
        onDismissRequest = onCerrar,
        title = {Text("Estas seguro de eliminar la tarea?")},
        confirmButton = {
            Button(onClick = {
                onConfirmar()
                onCerrar()
            }) {
                Text("Si")
            }
        },
        dismissButton = {
            Button(onClick = {
                onCerrar()
            }) {
                Text("no")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun selectorFecha(
    formatoFecha: SimpleDateFormat,
    onConfirm:(String) -> Unit,
    onCerrar: () -> Unit
){
    val datePickerState = rememberDatePickerState(
        initialDisplayMode = DisplayMode.Picker,
    )
    DatePickerDialog(
        onDismissRequest = onCerrar,
        confirmButton = {
            Button(onClick = {
                val selectedDateMillis = datePickerState.selectedDateMillis
                selectedDateMillis?.let {
                    onConfirm(formatoFecha.format(Date(it)))
                }
                onCerrar()
            }) {
                Text("Confirm")
            }

        },
        dismissButton = {
            Button(onClick = onCerrar) {
                Text("Cancel")
            }
        }
    ) {
        androidx.compose.material3.DatePicker(state = datePickerState)
    }
}