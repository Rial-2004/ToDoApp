package dev.rialmar.todoapp.views


import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import dev.rialmar.todoapp.R
import dev.rialmar.todoapp.data.FirebaseViewModel

@Composable
fun login(
    modo: Boolean,
    navController: NavController
) {
    val context = LocalContext.current
    val firebaseFunciones: FirebaseViewModel = viewModel()

    //Esto es la huella digital que se necesita para que google identifique mi aplicacion
    val webClientId = "475419254994-8na4lh7b6nk0muhlehk1pmcbgvmfq0hs.apps.googleusercontent.com"

    //Con esto le pedimos que cuando seleccione una cuenta nos de el email
    val emailGoogle = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()
    }

    //Cliente de google, se encarga de la comunicacion y le pasamos como parametro el emailGoogle
    val clienteGoogle = remember { GoogleSignIn.getClient(context, emailGoogle) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        //Esto hace que el usuario al seleccionar una cuenta vuelva a la aplicacion con los datos que tiene guardados
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            //Aqui abrimos el paquete para obtener el recultado
            val account = task.getResult(ApiException::class.java)

            //Obtenemos el token de la cuenta que selecciono el usuario
            account?.idToken?.let {
                //Llamamos a la funcion de firebase para que inicie sesion con google
                //pasando como parametro el token que nos da google
                firebaseFunciones.signInWithGoogle(it) { error ->
                    //Si todo va bien recibiremos un null y navegamos a la pantalla de tareas
                    if (error == null) {
                        navController.navigate("Tareas")
                    } else {
                        //Si hay un error mostramos un toast con el error
                        Toast.makeText(context, "Error Firebase: $error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } catch (e: ApiException) {
            //En caso de haber algun durante el proceso de inicio de sesion saltaria un toast con el error
            Toast.makeText(context, "Error Google: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    var imagen by remember { mutableStateOf(R.drawable.logo_black) }
    var imagenText by remember { mutableStateOf(R.drawable.logo_text_black) }
    if (modo) {
        imagen = R.drawable.logo_white
        imagenText = R.drawable.logo_text_white
    } else {
        imagen = R.drawable.logo_black
        imagenText = R.drawable.logo_text_black
    }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("LOGIN", modifier = Modifier.padding(10.dp), fontWeight = FontWeight.Bold)
            Column(
                modifier = Modifier
                    .shadow(12.dp, shape = RoundedCornerShape(12.dp))
                    .background(
                        MaterialTheme.colorScheme.onBackground, shape = RoundedCornerShape(12.dp)
                    )
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(imagen),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
                Image(painter = painterResource(imagenText), contentDescription = null)
                Spacer(modifier = Modifier.height(15.dp))
                campoDeTexto(email, "Email", onChangeText = { email = it })
                campoDeTexto(password, "Password", onChangeText = { password = it })
                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            firebaseFunciones.signIn(
                                email = email,
                                password = password,
                                respuesta = {
                                    if (it == null) {
                                        navController.navigate("Tareas")
                                    } else {
                                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                        email = ""
                                        password = ""
                                    }

                                })
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text("Aceptar")
                }
                Spacer(modifier = Modifier.height(10.dp))
                TextButton(onClick = {
                    navController.navigate("signIn")
                }) {
                    Text("Registrarse")
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { launcher.launch(clienteGoogle.signInIntent) },
                    modifier = Modifier.width(200.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google),
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

    }
}

@Composable
fun signIn(
    modo: Boolean,
    navController: NavController
) {
    val context = LocalContext.current
    val firebaseFunciones: FirebaseViewModel = viewModel()
    var imagen by remember { mutableStateOf(R.drawable.logo_black) }
    var imagenText by remember { mutableStateOf(R.drawable.logo_text_black) }
    if (modo) {
        imagen = R.drawable.logo_white
        imagenText = R.drawable.logo_text_white
    } else {
        imagen = R.drawable.logo_black
        imagenText = R.drawable.logo_text_black
    }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "SIGN IN",
                modifier = Modifier.padding(10.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Column(
                modifier = Modifier
                    .shadow(12.dp, shape = RoundedCornerShape(12.dp))
                    .background(
                        MaterialTheme.colorScheme.onBackground, shape = RoundedCornerShape(12.dp)
                    )
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(imagen),
                    contentDescription = null,
                    modifier = Modifier.size(150.dp)
                )
                Image(painter = painterResource(imagenText), contentDescription = null)
                Spacer(modifier = Modifier.height(15.dp))
                campoDeTexto(email, "Email", onChangeText = { email = it })
                campoDeTexto(password, "Password", onChangeText = { password = it })
                Button(
                    onClick = {
                        if (email.isNotBlank() && password.isNotBlank()) {
                            firebaseFunciones.createUser(
                                email = email,
                                password = password,
                                respuesta = {
                                    if (it == null) {
                                        navController.navigate("login")
                                    } else {
                                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                                    }

                                })

                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text("Aceptar")
                }
            }
        }

    }
}

@Composable
fun campoDeTexto(texto: String, referencia: String, onChangeText: (String) -> Unit) {
    OutlinedTextField(
        value = texto,
        onValueChange = onChangeText,
        label = { Text(referencia, fontSize = 14.sp) },
        modifier = Modifier.width(200.dp),
        textStyle = TextStyle(fontSize = 14.sp),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        supportingText = {
            if (referencia == "Password") Text(
                "Requiere: 6+ caracteres, una mayúscula, una minúscula y un número",
                fontSize = 8.sp,
                lineHeight = 7.sp
            )
        }
    )
}