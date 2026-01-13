package dev.rialmar.todoapp.data

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class FirebaseViewModel() : ViewModel() {


    //tareasBrutas es la lista que se obtiene de la base de datos, es la que recibe directamente los datos
    private val tareasBrutas = MutableStateFlow<List<Tarea>>(emptyList())

    //tareasComposables son las tareas que usara mi compose para visualizar,
    val tareasComposables: StateFlow<List<Tarea>> = tareasBrutas
    //Se hace asi por lo que entendi porque noe s una buena practica manipular directamente los datos obtenidos de la base de datos.

    private val auth = Firebase.auth
    private val db = Firebase.firestore


    fun signIn(email: String, password: String, respuesta: (String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    respuesta(null)
                } else {
                    val exception = when (task.exception) {
                        is FirebaseAuthInvalidUserException -> "El usuario no existe"
                        is FirebaseAuthInvalidCredentialsException -> "Contraseña incorrecta o usuario incorrecto"
                        else -> "Error al iniciar sesión"
                    }
                    respuesta(exception)
                }
            }
    }

    fun createUser(email: String, password: String, respuesta: (String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    respuesta(null)
                } else {
                    val exception = when (task.exception) {
                        is FirebaseAuthWeakPasswordException -> "La contraseña debe tener al menos 6 caracteres"
                        is FirebaseAuthUserCollisionException -> "El usuario ya existe"
                        is FirebaseAuthInvalidCredentialsException -> "El email no es valido"
                        else -> "Error al iniciar sesión"
                    }
                    respuesta(exception)
                }
            }
    }

    fun createTarea(
        userUID: String,
        titulo: String,
        descripcion: String,
        fecha: String,
        resultado: (String) -> Unit
    ) {
        //Con esto obtengo la ruta al documento que estoy creando
        val ruta = db.collection("usuarios")
            .document(userUID)
            .collection("tareas")
            .document()

        //Del documento obtengo su ID unico para ponerselo al atributo id de Tarea, asi despues al momento de eliminar una tarea solo necesito saber el atributo para referenciar al documento en la base de datos
        val tarea = Tarea(
            id = ruta.id,
            titulo = titulo,
            descripcion = descripcion,
            fecha = fecha
        )
        ruta.set(tarea)
            .addOnSuccessListener {
                resultado("Tarea creada correctamente")
            }.addOnFailureListener {
                resultado("Error al crear la tarea")
            }
    }

    fun deleteTarea(userUID: String, tareaID: String, resultado: (String) -> Unit) {
        db.collection("usuarios")
            .document(userUID)
            .collection("tareas")
            .document(tareaID)
            .delete()
            .addOnSuccessListener {
                resultado("Tarea eliminada correctamente")
            }.addOnFailureListener {
                resultado("Error al eliminar la tarea")
            }
    }

    // Creamos una funcion que devuelve un Flow<List> que constantemente se actualiza con los datos de la base de datos gracias al callbackFlow

    fun flujoObtenerTareas(userUID: String): Flow<List<Tarea>> = callbackFlow {

        val subscription = db.collection("usuarios")
            .document(userUID)
            .collection("tareas")
            .addSnapshotListener { snapshot, error ->
                //Snapshot es el valor del documento como tal, es como hacer un for-each de los documentos de la carpeta "tareas"
                //Error es el error que puede llegar a ocurrir, si no hay error su valor el null
                if (error != null) {
                    //Si hay un error cortamos el flujo de datos y cerramos la conexion a la base de datos
                    close(error)
                    return@addSnapshotListener
                }

                //Creamos una lista de tareas pasando el valor de los documentos a un clase tarea
                val tareas = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Tarea::class.java)
                } ?: emptyList() // En caso de estar vacio o null retornamos una lista vacia

                //trySend, para pasar el valor por el Flow, al ser un Flow como una tuberia que pasa datos de forma constante no mandamos algo una vez
                trySend(tareas)
            }
        //Esto es para evitar que cada vez que el susuario entre a la pantalla de tareas no cree una nueva conexion con la base de datos
        awaitClose { subscription.remove() }
    }

    fun cargarListaDelUsuario(userUID: String) {
        viewModelScope.launch {
            flujoObtenerTareas(userUID).collect {
                tareasBrutas.value = it
            }
        }
    }

    fun exportarTareas(context: Context): Boolean {
        try {
            val contenido = tareasComposables.value.joinToString("\n") {
                "id: ${it.id} \n titulo: ${it.titulo} \n descripcion: ${it.descripcion} \n fecha: ${it.fecha} \n fechaCreacion: ${it.fechaCreacion}"
            }
            val resolver = context.contentResolver
            val archivo = ContentValues().apply {
                put(
                    MediaStore.MediaColumns.DISPLAY_NAME,
                    "tareas_${System.currentTimeMillis()}.txt"
                )
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            //Le dice al dispositivo que insertara un archivo en la carpeta de descargas
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, archivo)

            uri?.let {
                //Abre la conecion para pasar datos al archivo
                //use hace que una vez terminado el flujo de datos se cierre la conexion
                resolver.openOutputStream(it)?.use { outputStream ->
                    //Pasame el contenido a bytes y escribimos en el archivo
                    outputStream.write(contenido.toByteArray())
                }
            }
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun signInWithGoogle(idToken: String, respuesta: (String?) -> Unit) {
        //Creamos una credencia de google con el token que nos da google
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener {
                //Si el proceso se realizo correctamente devolvemos un null, si no un mensaje de error
                if (it.isSuccessful) {
                    respuesta(null)
                } else {
                    respuesta(it.exception?.localizedMessage)
                }
            }
    }


}