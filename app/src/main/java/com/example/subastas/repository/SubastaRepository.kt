package com.example.subastas.repository

import com.example.subastas.data.model.Puja
import com.example.subastas.data.model.Subasta
import kotlinx.coroutines.delay

// Qué hace: Abstrae el origen de datos (API simulada) y gestiona la lógica de datos.
class SubastaRepository {

    // Simulación de una base de datos en memoria.
    private val subastasSimuladas = mutableListOf(
        Subasta(1, "Subasta 1", "25-12-2025", 100000.0, 21, "url_imagen_1"),
        Subasta(2, "Subasta 2", "25-12-2025", 80000.0, 55, "url_imagen_2"),
        Subasta(3, "Subasta 3", "26-12-2025", 120000.0, 21, "url_imagen_3"),
        Subasta(4, "Subasta 4", "26-12-2025", 120300.0, 21, "url_imagen_4")
    )

    // Qué hace: Simula la obtención de subastas desde una API.
    suspend fun getSubastas(query: String?): List<Subasta> {
        delay(1000) // Simula latencia de red
        return if (query.isNullOrBlank()) {
            subastasSimuladas
        } else {
            subastasSimuladas.filter { it.nombre.contains(query, ignoreCase = true) }
        }
    }

    // Qué hace: Simula la obtención del detalle de una subasta.
    suspend fun getSubastaDetail(id: Int): Subasta? {
        delay(500) // Simula latencia de red
        return subastasSimuladas.find { it.id == id }
    }

    // Qué hace: Simula el envío de una puja a la API.
    suspend fun realizarPuja(puja: Puja): Boolean {
        delay(1500) // Simula latencia de red para un POST
        val subasta = subastasSimuladas.find { it.id == puja.subastaId }
        if (subasta != null && puja.monto > subasta.ofertaMaxima) {
            val index = subastasSimuladas.indexOf(subasta)
            subastasSimuladas[index] = subasta.copy(ofertaMaxima = puja.monto)
            return true // Simula una respuesta exitosa
        }
        return false // Simula un fallo
    }

    // Qué hace: Simula la creación de una nueva subasta.
    suspend fun crearSubasta(nombre: String, fecha: String, ofertaMinima: Double): Subasta {
        delay(1000)
        val nuevaSubasta = Subasta(
            id = subastasSimuladas.size + 1,
            nombre = nombre,
            fecha = fecha,
            ofertaMaxima = ofertaMinima,
            inscritos = 0, // Las subastas nuevas inician sin inscritos
            imagenUrl = null // La imagen se manejaría por separado
        )
        subastasSimuladas.add(nuevaSubasta)
        return nuevaSubasta
    }
}