package com.example.subastas.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Puja(
    val subastaId: Int,
    val monto: Double
)
