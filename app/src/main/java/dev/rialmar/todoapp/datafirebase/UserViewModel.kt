package dev.rialmar.todoapp.datafirebase;

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.auth

class UserViewModel: ViewModel() {
    private val auth = Firebase.auth

    fun loginIn(email: String,password: String, resultado: (String?) -> Unit){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    resultado(null);
                }else{
                    val exception = task.exception
                    resultado(when(exception){
                        is FirebaseAuthInvalidUserException -> "Usuario no existente"
                        is FirebaseAuthInvalidCredentialsException -> "Credenciales inválidas"
                        is FirebaseNetworkException -> "Error de conexión"
                        else -> "Error desconocido"
                    })
                }
            }
    }

    fun registerIn(email: String,password: String, resultado: (String?) -> Unit){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful){
                    resultado(null);
                }else{
                    val exception = task.exception
                    resultado(when(exception){
                        is FirebaseAuthInvalidUserException -> "Usuario no existente"
                        is FirebaseAuthInvalidCredentialsException -> "Credenciales inválidas"
                        is FirebaseNetworkException -> "Error de conexión"
                        else -> "Error desconocido"
                    })
                }
            }
    }
}
