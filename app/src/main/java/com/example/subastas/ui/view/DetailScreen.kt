package com.example.subastas.ui.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.subastas.R
import com.example.subastas.data.model.Subasta
import com.example.subastas.viewmodel.SubastaViewModel
import com.example.subastas.viewmodel.UiState
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(subastaId: Int, viewModel: SubastaViewModel, navController: NavController) {
    val uiState by viewModel.subastaDetailState.collectAsState()
    val context = LocalContext.current

    var pujaAmount by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showFinishDialog by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = subastaId) {
        viewModel.loadSubastaDetail(subastaId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.data?.nombre ?: "Detalle de Subasta") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
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
        HandleDetailUiState(
            uiState = uiState,
            paddingValues = paddingValues,
            pujaAmount = pujaAmount,
            onPujaAmountChange = { pujaAmount = it },
            onRealizarPuja = {
                viewModel.realizarPuja(subastaId, pujaAmount) { result ->
                    result.onSuccess {
                        Toast.makeText(context, "Puja realizada con éxito", Toast.LENGTH_SHORT).show()
                        pujaAmount = ""
                    }.onFailure {
                        Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            onFinalizarClick = { showFinishDialog = true },
            onEliminarClick = { showDeleteDialog = true }
        )

        if (showDeleteDialog) {
            ConfirmDialog(
                title = "Eliminar Subasta",
                text = "¿Estás seguro de que quieres eliminar esta subasta? Esta acción no se puede deshacer.",
                onConfirm = {
                    viewModel.eliminarSubasta(subastaId) { result ->
                        result.onSuccess {
                            Toast.makeText(context, "Subasta eliminada", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }.onFailure {
                            Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                    showDeleteDialog = false
                },
                onDismiss = { showDeleteDialog = false }
            )
        }
        if (showFinishDialog) {
            ConfirmDialog(
                title = "Finalizar Subasta",
                text = "¿Estás seguro de que quieres finalizar esta subasta? Nadie más podrá ofertar.",
                onConfirm = {
                    viewModel.finalizarSubasta(subastaId) { result ->
                        result.onSuccess {
                            Toast.makeText(context, "Subasta finalizada", Toast.LENGTH_SHORT).show()
                        }.onFailure {
                            Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                    showFinishDialog = false
                },
                onDismiss = { showFinishDialog = false }
            )
        }
    }
}

@Composable
private fun HandleDetailUiState(
    uiState: UiState<Subasta>,
    paddingValues: PaddingValues,
    pujaAmount: String,
    onPujaAmountChange: (String) -> Unit,
    onRealizarPuja: () -> Unit,
    onFinalizarClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            uiState.error != null -> {
                Text(
                    "Error: ${uiState.error}",
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp)
                )
            }
            uiState.data != null -> {
                SubastaDetailContent(
                    subasta = uiState.data,
                    pujaAmount = pujaAmount,
                    onPujaAmountChange = onPujaAmountChange,
                    onRealizarPuja = onRealizarPuja,
                    onFinalizarClick = onFinalizarClick,
                    onEliminarClick = onEliminarClick
                )
            }
        }
    }
}

@Composable
fun SubastaDetailContent(
    subasta: Subasta,
    pujaAmount: String,
    onPujaAmountChange: (String) -> Unit,
    onRealizarPuja: () -> Unit,
    onFinalizarClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
    currencyFormat.maximumFractionDigits = 0
    val isClosed = subasta.estado == "Cerrada"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(subasta.nombre, fontSize = 26.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text("Fecha: ${subasta.fecha}", fontSize = 16.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(subasta.imagenUrl)
                .crossfade(true)
                .error(R.drawable.ic_launcher_foreground) // Placeholder en caso de error
                .build(),
            contentDescription = "Imagen de la subasta",
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Oferta Máxima Actual:", fontSize = 18.sp, color = Color.Gray)
                Text(
                    currencyFormat.format(subasta.ofertaMaxima),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Inscritos: ${subasta.inscritos}", fontSize = 16.sp)
                Text(
                    "Estado: ${subasta.estado}",
                    fontSize = 16.sp,
                    color = if(isClosed) Color.Red else Color(0xFF388E3C)
                )
            }
        }

        if (isClosed) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Esta subasta ha finalizado.", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Red)
        } else {
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = pujaAmount,
                onValueChange = onPujaAmountChange,
                label = { Text("Ingresa tu puja/oferta") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRealizarPuja,
                modifier = Modifier.fillMaxWidth(),
                enabled = pujaAmount.isNotBlank()
            ) {
                Text("Pujar / Ofertar")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (!isClosed) {
                Button(
                    onClick = onFinalizarClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Finalizar")
                }
            }
            Button(
                onClick = onEliminarClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
            ) {
                Text("Eliminar")
            }
        }
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = text) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}