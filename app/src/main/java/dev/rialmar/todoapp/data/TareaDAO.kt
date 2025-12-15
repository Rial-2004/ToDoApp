package dev.rialmar.todoapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDAO {
    @Query("SELECT * FROM tarea ORDER BY id ASC")
    fun obtenerTareas(): Flow<List<Tarea>>

    @Query("SELECT * FROM tarea ORDER BY id ASC")
    suspend fun obtenerListaParaExportar(): List<Tarea>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregar(tarea: Tarea)

    @Delete
    suspend fun eliminar(tarea: Tarea)
}