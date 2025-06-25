package com.example.subastas.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.subastas.data.model.Subasta
import com.example.subastas.viewmodel.SubastaViewModel

// Qué hace: Muestra la pantalla principal con la lista de subastas.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: SubastaViewModel) {
    val subastasList by viewModel.subastasList.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subastas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("create_auction") }) {
                Text("Nuevo", modifier = Modifier.padding(16.dp))
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                label = { Text("Search by name or date") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { viewModel.loadSubastas(searchQuery.ifBlank { null }) },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Search")
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Nombre", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("Oferta Máxima", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        Text("Inscritos", fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.5f))
                        Text("Fecha Rifa", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    }
                    Divider()
                }
                items(subastasList) { subasta ->
                    SubastaItem(subasta = subasta) {
                        navController.navigate("detail/${subasta.id}")
                    }
                }
            }
        }
    }
}

// Qué hace: Muestra un único ítem de subasta en la lista.
@Composable
fun SubastaItem(subasta: Subasta, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(subasta.nombre, modifier = Modifier.weight(1f))
        Text(String.format("$%,.0f", subasta.ofertaMaxima), modifier = Modifier.weight(1f))
        Text(subasta.inscritos.toString(), modifier = Modifier.weight(0.5f))
        Text(subasta.fecha, modifier = Modifier.weight(1f))
    }
    Divider()
}