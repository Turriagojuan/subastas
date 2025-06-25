package com.example.subastas.data.remote

import com.example.subastas.data.model.Puja
import com.example.subastas.data.model.Subasta
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// Qué hace: Define los endpoints de la API REST para interactuar con el servidor.
interface ApiService {

    // Qué hace: Obtiene la lista de todas las subastas o busca por nombre.
    @GET("subastas")
    suspend fun getSubastas(@Query("nombre") nombre: String?): Response<List<Subasta>>

    // Qué hace: Obtiene el detalle de una subasta específica por su ID.
    @GET("subastas/{id}")
    suspend fun getSubastaDetail(@Path("id") id: Int): Response<Subasta>

    // Qué hace: Envía una nueva puja al servidor.
    @POST("pujas")
    suspend fun realizarPuja(@Body puja: Puja): Response<Unit>

    // Qué hace: Crea una nueva subasta en el servidor.
    @POST("subastas")
    suspend fun crearSubasta(@Body subasta: Subasta): Response<Subasta>
}