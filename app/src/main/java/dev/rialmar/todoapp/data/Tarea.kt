package dev.rialmar.todoapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tarea")
data class Tarea(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "titulo")
    val titulo: String,

    @ColumnInfo(name = "descripcion")
    val descripcion: String,

    @ColumnInfo(name = "fecha")
    val fecha: String
)
