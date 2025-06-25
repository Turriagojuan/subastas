package com.example.subastas.ui.view

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.subastas.viewmodel.SubastaViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAuctionScreen(navController: NavController, viewModel: SubastaViewModel) {
    var nombreSubasta by remember { mutableStateOf("") }
    var fechaSubasta by remember { mutableStateOf("") }
    var ofertaMinima by remember { mutableStateOf("") }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imagenUri = uri
    }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            // Formateamos la fecha a dd-MM-yyyy
            fechaSubasta = String.format("%02d-%02d-%d", dayOfMonth, month + 1, year)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear nueva Subasta") },
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = nombreSubasta,
                onValueChange = { nombreSubasta = it },
                label = { Text("Nombre Subasta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = fechaSubasta,
                onValueChange = { },
                label = { Text("Fecha de subasta") },
                placeholder = { Text("dd-MM-yyyy") },
                modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() },
                readOnly = true,
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha") }
            )

            OutlinedTextField(
                value = imagenUri?.lastPathSegment ?: "",
                onValueChange = {},
                label = { Text("Imagen del elemento a subastar") },
                readOnly = true,
                trailingIcon = {
                    Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Text("Archivo")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = ofertaMinima,
                onValueChange = { ofertaMinima = it },
                label = { Text("Oferta Mínima") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    viewModel.crearSubasta(
                        nombre = nombreSubasta,
                        fecha = fechaSubasta,
                        ofertaMinimaStr = ofertaMinima,
                        // En una app real, aquí subirías la imagen a un servidor y obtendrías una URL
                        imagenUrl = "https://picsum.photos/seed/${nombreSubasta.replace(" ", "")}/400/300"
                    ) { result ->
                        result.onSuccess {
                            Toast.makeText(context, "Subasta creada con éxito", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }.onFailure {
                            Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = nombreSubasta.isNotBlank() && fechaSubasta.isNotBlank() && ofertaMinima.isNotBlank()
            ) {
                Text("Guardar")
            }
        }
    }
}