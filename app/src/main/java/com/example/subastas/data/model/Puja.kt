package com.example.subastas.data.model

import kotlinx.serialization.Serializable

// Qué hace: Define la estructura de datos para una puja.
@Serializable
data class Puja(
    val subastaId: Int,
    val monto: Double
)