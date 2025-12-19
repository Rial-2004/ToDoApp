package dev.rialmar.todoapp.datafirebase

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class DataViewModel: ViewModel() {
    private val auth = Firebase.auth
    fun signIn(email: String, password: String, resultado:(String?) -> Unit){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    resultado(null)
                }else{
                    val exception = when(task.exception){
                        is a -> ""
                    }
                    resultado(
                        exception
                    )
                }
            }
    }
}