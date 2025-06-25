package com.example.subastas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subastas.data.model.Puja
import com.example.subastas.data.model.Subasta
import com.example.subastas.repository.SubastaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Qué hace: Contiene la lógica de la UI y gestiona el estado para las vistas de subastas.
class SubastaViewModel(private val repository: SubastaRepository = SubastaRepository()) : ViewModel() {

    // Flujo de estado para la lista de subastas
    private val _subastasList = MutableStateFlow<List<Subasta>>(emptyList())
    val subastasList: StateFlow<List<Subasta>> = _subastasList

    // Flujo de estado para el detalle de una subasta
    private val _subastaDetail = MutableStateFlow<Subasta?>(null)
    val subastaDetail: StateFlow<Subasta?> = _subastaDetail

    // Flujo de estado para la búsqueda
    val searchQuery = MutableStateFlow("")

    init {
        loadSubastas()
    }

    // Qué hace: Carga o busca subastas desde el repositorio.
    fun loadSubastas(query: String? = null) {
        viewModelScope.launch {
            val result = repository.getSubastas(query)
            _subastasList.value = result
        }
    }

    // Qué hace: Carga los detalles de una subasta específica.
    fun loadSubastaDetail(id: Int) {
        viewModelScope.launch {
            _subastaDetail.value = repository.getSubastaDetail(id)
        }
    }

    // Qué hace: Procesa el envío de una nueva puja.
    fun realizarPuja(subastaId: Int, monto: Double) {
        viewModelScope.launch {
            val puja = Puja(subastaId, monto)
            val success = repository.realizarPuja(puja)
            if (success) {
                // Si la puja es exitosa, recargamos los detalles para mostrar la nueva oferta máxima
                loadSubastaDetail(subastaId)
            }
        }
    }

    // Qué hace: Crea una nueva subasta.
    fun crearSubasta(nombre: String, fecha: String, ofertaMinima: Double, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                repository.crearSubasta(nombre, fecha, ofertaMinima)
                onResult(true)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}