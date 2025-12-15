package dev.rialmar.todoapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TareaViewModelFactory(private val tareaDao: TareaDAO) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TareaViewModel::class.java)) {
            return TareaViewModel(tareaDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

