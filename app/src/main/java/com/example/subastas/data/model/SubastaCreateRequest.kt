package com.example.subastas.data.model

import kotlinx.serialization.Serializable

@Serializable
data class SubastaCreateRequest(
    val nombre: String,
    val fecha: String,
    val ofertaMinima: Double,
    val imagenUrl: String?
)
