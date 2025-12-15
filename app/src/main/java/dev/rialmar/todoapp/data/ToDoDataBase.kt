package dev.rialmar.todoapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Tarea::class],
    version = 1
)
abstract class ToDoDataBase: RoomDatabase (){
    abstract fun tareaDAO(): TareaDAO

    companion object{
        @Volatile
        private var INSTANCE: ToDoDataBase? = null

        fun getBase(context: Context): ToDoDataBase{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDataBase::class.java,
                    "ToDoAppBaseDeDatos"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}