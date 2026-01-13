package dev.rialmar.todoapp.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Tarea(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: String = "",
    val fechaCreacion: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
)
