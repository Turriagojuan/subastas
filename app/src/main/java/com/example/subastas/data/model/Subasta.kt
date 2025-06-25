package com.example.subastas.data.model

import kotlinx.serialization.Serializable

// Qué hace: Define la estructura de datos para una subasta.
@Serializable
data class Subasta(
    val id: Int,
    val nombre: String,
    val fecha: String,
    val ofertaMaxima: Double,
    val inscritos: Int,
    val imagenUrl: String? = null
)