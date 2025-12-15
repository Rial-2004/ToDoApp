package dev.rialmar.todoapp.views


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.rialmar.todoapp.R

@Composable
fun login(
    onCambioNombre: (String) -> Unit,
    onCambioAlias: (String) -> Unit,
    navController: NavController,
    modo: Boolean
){
    var imagen by remember { mutableStateOf(R.drawable.logo_black) }
    var imagenText by remember { mutableStateOf(R.drawable.logo_text_black) }
    if (modo){
        imagen = R.drawable.logo_white
        imagenText = R.drawable.logo_text_white
    }else{
        imagen = R.drawable.logo_black
        imagenText = R.drawable.logo_text_black
    }
    var auxN by remember { mutableStateOf("") }
    var auxA by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
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
            CampoDeTexto(auxN, "Nombre", onChangeText = { auxN = it })
            CampoDeTexto(auxA, "Alias", onChangeText = { auxA = it })
            Button(onClick = {
                if (auxN.isNotBlank() && auxA.isNotBlank()) {
                    onCambioNombre(auxN)
                    onCambioAlias(auxA)
                    navController.navigate("tareas")
                }
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )) {
                Text("Aceptar")

            }
        }
    }
}

@Composable
fun CampoDeTexto(texto: String, referencia: String, onChangeText: (String) -> Unit){
    OutlinedTextField(
        value = texto,
        onValueChange = onChangeText,
        label = { Text(referencia, fontSize = 14.sp) },
        modifier = Modifier.width(200.dp),
        textStyle = TextStyle(fontSize = 14.sp),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}