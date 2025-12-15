package dev.rialmar.todoapp.data

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.miDataStore by preferencesDataStore("settings")

object PreferencesKeys{
    val ColorTexto = intPreferencesKey("color_texto")
    val ModoOscuro = booleanPreferencesKey("modo_oscuro")
}
class SettingsRepository(private val context: Context){
    suspend fun guardarColor(color: Color){
        context.miDataStore.edit { aux ->
            aux[PreferencesKeys.ColorTexto] = color.toArgb()
        }
    }
    fun obtenerColor(): Flow<Color> {
        return context.miDataStore.data.map { aux ->
            val colorInt = aux[PreferencesKeys.ColorTexto] ?: Color.White.toArgb()
            Color(colorInt)
        }
    }

    suspend fun guardarModoOscuro(modo: Boolean){
        context.miDataStore.edit { aux ->
            aux[PreferencesKeys.ModoOscuro] = modo
        }
    }

    fun obtenerModoOscuro(): Flow<Boolean>{
        return context.miDataStore.data.map { aux ->
            aux[PreferencesKeys.ModoOscuro] ?: false
        }
    }

}