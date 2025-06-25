package com.example.subastas.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.subastas.R
import com.example.subastas.viewmodel.SubastaViewModel

// Qué hace: Muestra la pantalla de detalle de una subasta y permite realizar pujas.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(subastaId: Int, viewModel: SubastaViewModel, navController: NavController) {
    // Cargar detalles cuando la pantalla se muestra por primera vez
    LaunchedEffect(key1 = subastaId) {
        viewModel.loadSubastaDetail(subastaId)
    }

    val subasta by viewModel.subastaDetail.collectAsState()
    var pujaAmount by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(subasta?.nombre ?: "Detalle de Subasta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        subasta?.let {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(it.nombre, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // Placeholder para la imagen
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Imagen de la subasta",
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Oferta Máxima Actual: ${String.format("$%,.0f", it.ofertaMaxima)}", fontSize = 18.sp)
                Spacer(modifier = Modifier.height(24.dp))
                OutlinedTextField(
                    value = pujaAmount,
                    onValueChange = { pujaAmount = it },
                    label = { Text("300.000") },
                    placeholder = { Text("Ingresa tu puja/oferta") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        val monto = pujaAmount.toDoubleOrNull()
                        if (monto != null) {
                            viewModel.realizarPuja(it.id, monto)
                        }
                    }) {
                        Text("Guardar")
                    }
                    Button(onClick = { /* Lógica para finalizar */ }) {
                        Text("Finalizar")
                    }
                    Button(
                        onClick = { /* Lógica para eliminar */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Eliminar")
                    }
                }
            }
        } ?: run {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}