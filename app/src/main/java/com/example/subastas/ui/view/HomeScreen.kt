package com.example.subastas.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.subastas.data.model.Subasta
import com.example.subastas.viewmodel.SubastaViewModel
import com.example.subastas.viewmodel.UiState
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: SubastaViewModel) {
    val uiState by viewModel.subastasListState.collectAsState()
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
                Icon(Icons.Filled.Add, contentDescription = "Crear Subasta")
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text("Buscar por nombre") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            HandleUiState(uiState, navController)
        }
    }
}

@Composable
private fun HandleUiState(uiState: UiState<List<Subasta>>, navController: NavController) {
    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        uiState.error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${uiState.error}", color = Color.Red, textAlign = TextAlign.Center)
            }
        }
        uiState.data != null -> {
            if (uiState.data.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron subastas.")
                }
            } else {
                SubastasList(subastasList = uiState.data, navController = navController)
            }
        }
    }
}

@Composable
fun SubastasList(subastasList: List<Subasta>, navController: NavController) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Nombre", fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                Text("Oferta Máxima", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.5f), textAlign = TextAlign.End)
                Text("Estado", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
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

@Composable
fun SubastaItem(subasta: Subasta, onClick: () -> Unit) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    currencyFormat.maximumFractionDigits = 0

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(2f)) {
                Text(subasta.nombre, fontWeight = FontWeight.Bold)
                Text(subasta.fecha, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                currencyFormat.format(subasta.ofertaMaxima),
                modifier = Modifier.weight(1.5f),
                textAlign = TextAlign.End
            )
            Text(
                subasta.estado,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = if(subasta.estado == "Abierta") Color(0xFF388E3C) else Color.Red
            )
        }
    }
}