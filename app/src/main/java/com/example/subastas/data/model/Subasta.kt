package com.example.subastas.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Subasta(
    val id: Int,
    val nombre: String,
    val fecha: String,
    val ofertaMaxima: Double,
    val inscritos: Int,
    val imagenUrl: String? = null,
    val estado: String = "Abierta" // Puede ser "Abierta" o "Cerrada"
)
