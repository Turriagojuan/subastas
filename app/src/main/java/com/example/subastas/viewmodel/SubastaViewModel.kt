package com.example.subastas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subastas.data.model.Puja
import com.example.subastas.data.model.Subasta
import com.example.subastas.data.model.SubastaCreateRequest
import com.example.subastas.repository.SubastaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class UiState<T>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SubastaViewModel @Inject constructor(
    private val repository: SubastaRepository
) : ViewModel() {

    private val _subastasListState = MutableStateFlow(UiState<List<Subasta>>())
    val subastasListState: StateFlow<UiState<List<Subasta>>> = _subastasListState.asStateFlow()

    private val _subastaDetailState = MutableStateFlow(UiState<Subasta>())
    val subastaDetailState: StateFlow<UiState<Subasta>> = _subastaDetailState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // --- INICIO DE CAMBIOS ---

    // 1. Guardar una lista completa de subastas para filtrar localmente.
    private val allSubastas = mutableListOf<Subasta>()

    init {
        // 2. Cargar todas las subastas al iniciar el ViewModel.
        loadSubastas()
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        // 3. Filtrar la lista localmente en lugar de llamar a la API cada vez.
        // La búsqueda no distingue entre mayúsculas y minúsculas y busca si el nombre "contiene" el texto.
        val filteredList = if (query.isBlank()) {
            allSubastas
        } else {
            allSubastas.filter { it.nombre.contains(query, ignoreCase = true) }
        }
        _subastasListState.value = UiState(data = filteredList)
    }

    // 4. Se modifica loadSubastas para que no acepte una query y siempre cargue todo.
    fun loadSubastas() {
        viewModelScope.launch {
            _subastasListState.value = UiState(isLoading = true)
            // Llamar al repositorio sin parámetros para obtener todas las subastas.
            val result = repository.getSubastas(null)
            result.onSuccess { subastas ->
                allSubastas.clear()
                allSubastas.addAll(subastas)
                _subastasListState.value = UiState(data = allSubastas)
            }.onFailure { error ->
                _subastasListState.value = UiState(error = error.message)
            }
        }
    }

    // --- FIN DE CAMBIOS ---


    fun loadSubastaDetail(id: Int) {
        viewModelScope.launch {
            _subastaDetailState.value = UiState(isLoading = true)
            val result = repository.getSubastaDetail(id)
            result.onSuccess { subasta ->
                _subastaDetailState.value = UiState(data = subasta)
            }.onFailure { error ->
                _subastaDetailState.value = UiState(error = error.message)
            }
        }
    }

    fun realizarPuja(subastaId: Int, montoStr: String, onResult: (Result<Unit>) -> Unit) {
        val monto = montoStr.toDoubleOrNull()
        if (monto == null || monto <= 0) {
            onResult(Result.failure(Exception("El monto debe ser un número positivo.")))
            return
        }

        val subastaActual = _subastaDetailState.value.data
        if (subastaActual != null && monto <= subastaActual.ofertaMaxima) {
            onResult(Result.failure(Exception("La puja debe ser mayor a la oferta máxima actual.")))
            return
        }

        viewModelScope.launch {
            val puja = Puja(subastaId, monto)
            val result = repository.realizarPuja(puja)
            result.onSuccess { updatedSubasta ->
                _subastaDetailState.value = UiState(data = updatedSubasta)
                onResult(Result.success(Unit))
            }.onFailure { error ->
                onResult(Result.failure(error))
            }
        }
    }

    fun crearSubasta(
        nombre: String,
        fecha: String,
        ofertaMinimaStr: String,
        imagenUrl: String?,
        onResult: (Result<Unit>) -> Unit
    ) {
        if (nombre.isBlank() || fecha.isBlank() || ofertaMinimaStr.isBlank()) {
            onResult(Result.failure(Exception("Todos los campos son obligatorios.")))
            return
        }
        val ofertaMinima = ofertaMinimaStr.toDoubleOrNull()
        if (ofertaMinima == null || ofertaMinima <= 0) {
            onResult(Result.failure(Exception("La oferta mínima debe ser un número positivo.")))
            return
        }
        if (!isValidDate(fecha)) {
            onResult(Result.failure(Exception("El formato de fecha debe ser dd-MM-yyyy.")))
            return
        }

        viewModelScope.launch {
            val request = SubastaCreateRequest(nombre, fecha, ofertaMinima, imagenUrl)
            val result = repository.crearSubasta(request)
            result.onSuccess {
                loadSubastas() // Recargar la lista
                onResult(Result.success(Unit))
            }.onFailure { error ->
                onResult(Result.failure(error))
            }
        }
    }

    fun eliminarSubasta(id: Int, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = repository.eliminarSubasta(id)
            onResult(result)
        }
    }

    fun finalizarSubasta(id: Int, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = repository.finalizarSubasta(id)
            result.onSuccess { updatedSubasta ->
                _subastaDetailState.value = UiState(data = updatedSubasta)
                onResult(Result.success(Unit))
            }.onFailure { error ->
                onResult(Result.failure(error))
            }
        }
    }

    private fun isValidDate(dateStr: String): Boolean {
        val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        format.isLenient = false
        return try {
            format.parse(dateStr)
            true
        } catch (e: Exception) {
            false
        }
    }
}