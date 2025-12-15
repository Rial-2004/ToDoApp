package dev.rialmar.todoapp.views

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu

import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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


import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dev.rialmar.todoapp.NotificacionInactividadWorker
import dev.rialmar.todoapp.data.SettingsRepository
import dev.rialmar.todoapp.data.Tarea

import dev.rialmar.todoapp.data.TareaViewModel
import dev.rialmar.todoapp.data.TareaViewModelFactory
import dev.rialmar.todoapp.data.ToDoDataBase
import dev.rialmar.todoapp.data.exportarTareas
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

const val INACTIVITY_WORK_NAME = "InactivityReminderWork"
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Tareas(
    nombre:String,
    alias: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { SettingsRepository(context) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted){
            Toast.makeText(context, "Notificaciones activadas", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Notificaciones no activadas", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = android.Manifest.permission.POST_NOTIFICATIONS
            if (context.checkSelfPermission(permission) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    val colorTexto = repo.obtenerColor().collectAsState(initial = Color.White)
    val database = ToDoDataBase.getBase(context = context)
    val tareaDao = database.tareaDAO()

    val factory = TareaViewModelFactory(tareaDao = tareaDao)
    val viewModel: TareaViewModel = viewModel(factory = factory)

    val listaBase by viewModel.todasLasTareas.collectAsState(initial = emptyList())


    var searchTarea by remember { mutableStateOf("") }

    var tareaSeleccionada by remember { mutableStateOf<Tarea?>(null) }

    var expandir by remember { mutableStateOf(false) }
    var mostrarAddTarea by remember { mutableStateOf(false) }
    var mostrarDescripcion by remember { mutableStateOf(false) }
    var mostrarPreferencias by remember { mutableStateOf(false) }
    var mostrarDialogEliminar by remember { mutableStateOf(false) }


    val listaFiltrada = remember(listaBase, searchTarea) {
        if (searchTarea.isBlank()) {
            listaBase
        } else {
            listaBase.filter { tarea ->
                tarea.titulo.lowercase().startsWith(searchTarea.lowercase())
            }
        }
    }

    val opcionesMenu = mapOf(
        Icons.Default.Person to "Profile",
        Icons.Default.Settings to "Settings",
        Icons.Default.Star to "Export",
        Icons.Default.Info to "About"
    )


    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp, start = 15.dp, end = 15.dp)
                    .background(Color.Black, shape = RoundedCornerShape(20.dp)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "$nombre $alias",
                    color = Color.White,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(20.dp),
                    fontWeight = FontWeight.Bold
                )
                Box(modifier = Modifier.padding(end = 10.dp)) {
                    IconButton(onClick = {expandir = true}) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                    DropdownMenu(
                        expanded = expandir,
                        onDismissRequest = { expandir = false }) {
                        opcionesMenu.forEach { (icono, texto) ->
                            DropdownMenuItem(
                                onClick = {
                                    when(texto){
                                        "Settings" -> {
                                            mostrarPreferencias = true
                                        }
                                        "Export"->{
                                            scope.launch {
                                                val datosExportar = viewModel.obtenerDatosExportables()
                                                val exito = exportarTareas(context = context, datosExportar)
                                                if (exito) {
                                                    Toast.makeText(
                                                        context, "Tareas exportadas con exito",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }else{
                                                    Toast.makeText(
                                                        context, "Error al exportar las tareas",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        }
                                    }
                                },
                                text = { Text(texto) },
                                leadingIcon = {
                                    Icon(imageVector = icono, contentDescription = null)
                                }
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            IconButton(
                onClick = { mostrarAddTarea = true },
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .background(Color.Black, shape = RoundedCornerShape(20.dp))
                        .padding(10.dp)
                )
            }
        },
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (listaBase.count() > 0) {
                OutlinedTextField(
                    value = searchTarea,
                    onValueChange = { searchTarea = it },
                    label = { Text("Search") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    singleLine = true,
                    trailingIcon = {

                        if (searchTarea.isBlank()) {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.Search, contentDescription = null)
                            }

                        } else {
                            IconButton(onClick = { searchTarea = "" }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }

                        }
                    }
                )
            }
            LazyColumn(modifier = Modifier.height(600.dp)) {
                items(listaFiltrada) { tarea ->
                    ElevatedCard(
                        modifier = Modifier
                            .padding(top = 5.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.onBackground
                        ),
                        onClick = {
                            tareaSeleccionada = tarea
                            mostrarDescripcion = true
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        )
                        {
                            Column(modifier = Modifier.padding(15.dp)) {
                                Text(
                                    tarea.titulo,
                                    modifier = Modifier.padding(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorTexto.value
                                )
                                Text(
                                    tarea.fecha,
                                    fontSize = 10.sp,
                                    color = colorTexto.value
                                )
                            }

                            Row(modifier = Modifier.padding(15.dp)) {
                                IconButton(onClick = {}) {
                                    Icon(
                                        Icons.Default.Edit,
                                        contentDescription = "Editar tarea",
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(20.dp))
                                IconButton(onClick = {
                                    mostrarDialogEliminar = true
                                    tareaSeleccionada = tarea
                                }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Eliminar tarea",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (mostrarAddTarea) {
        dialogoNuevaTarea(
            onCerrar = { mostrarAddTarea = false },
            onConfirm = { tarea ->
                viewModel.agregarTarea(tarea = tarea)
            })
    }
    if (mostrarDescripcion && tareaSeleccionada != null) {
        tareaCompleta(tareaSeleccionada!!, onCerrar = { mostrarDescripcion = false })
    }

    if (mostrarPreferencias) {
        dialogoPreferencias(onCerrar = { mostrarPreferencias = false })
    }

    if (mostrarDialogEliminar && tareaSeleccionada != null){
        dialogoEliminar(onCerrar = {mostrarDialogEliminar = false}, onConfirmar = {(viewModel.eliminarTarea(tareaSeleccionada!!))})
    }
}
private fun reiniciarTemporizadorInactividad(context: Context) {
    val workManager = WorkManager.getInstance(context)


    val workRequest = OneTimeWorkRequestBuilder<NotificacionInactividadWorker>()
        .setInitialDelay(3, TimeUnit.MINUTES)
        .build()

    workManager.enqueueUniqueWork(
        INACTIVITY_WORK_NAME,
        ExistingWorkPolicy.REPLACE,
        workRequest
    )
}



