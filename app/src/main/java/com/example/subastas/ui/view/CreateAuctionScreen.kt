package com.example.subastas.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.subastas.viewmodel.SubastaViewModel

// Qué hace: Muestra el formulario para crear una nueva subasta.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAuctionScreen(navController: NavController, viewModel: SubastaViewModel) {
    var nombreSubasta by remember { mutableStateOf("") }
    var fechaSubasta by remember { mutableStateOf("") }
    var ofertaMinima by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear nueva Subasta") },
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombreSubasta,
                onValueChange = { nombreSubasta = it },
                label = { Text("Nombre Subasta") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = fechaSubasta,
                onValueChange = { fechaSubasta = it },
                label = { Text("Fecha de subasta") },
                placeholder = { Text("dd-MM-yyyy") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Imagen del elemento a subastar") },
                readOnly = true,
                trailingIcon = { Button(onClick = {}) { Text("Archivo") } },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ofertaMinima,
                onValueChange = { ofertaMinima = it },
                label = { Text("Oferta Mínima") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    val oferta = ofertaMinima.toDoubleOrNull()
                    if (nombreSubasta.isNotBlank() && fechaSubasta.isNotBlank() && oferta != null) {
                        viewModel.crearSubasta(nombreSubasta, fechaSubasta, oferta) { success ->
                            if (success) {
                                navController.popBackStack()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar")
            }
        }
    }
}