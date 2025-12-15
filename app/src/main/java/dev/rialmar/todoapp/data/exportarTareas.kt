package dev.rialmar.todoapp.data

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import okio.IOException

fun exportarTareas(context: Context, data: String): Boolean{
    val contenido = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME,"tareas_exportadas")
        put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }
    val resolver = context.contentResolver
    val coleccion = MediaStore.Downloads.EXTERNAL_CONTENT_URI
    var completado = false

    val insertar = resolver.insert(coleccion, contenido)

    if (insertar !=  null){
        try {
            resolver.openOutputStream(insertar).use { outputStream ->
                outputStream?.write(data.toByteArray())
                completado = true
            }
        }catch (e: IOException){

            resolver.delete(insertar, null, null)
            completado = false
        }
    }
    return completado
}