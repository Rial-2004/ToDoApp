package dev.rialmar.todoapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TareaViewModel(private val TareaDAO: TareaDAO) : ViewModel(){

    val todasLasTareas: Flow<List<Tarea>> = TareaDAO.obtenerTareas()

    fun agregarTarea(tarea: Tarea) {
        viewModelScope.launch {
            TareaDAO.agregar(tarea)
        }
    }

    fun eliminarTarea(tarea: Tarea) {
        // Ejecuta la operación de Room en un scope de coroutine
        viewModelScope.launch {
            TareaDAO.eliminar(tarea)
        }
    }

    suspend fun obtenerDatosExportables(): String {

        val tareas = TareaDAO.obtenerListaParaExportar()

        val titulo = "Tareas Exportadas"

        val tareasTexto = tareas.joinToString(separator = "\n") { tarea ->
            "ID: ${tarea.id}\nTitulo: ${tarea.titulo}\nDescripcion: ${tarea.descripcion}\nFecha: ${tarea.fecha}\n---------------"
        }

        return "$titulo\n---\n$tareasTexto"
    }

}
