package com.example.subastas.data.remote

import com.example.subastas.data.model.Puja
import com.example.subastas.data.model.Subasta
import com.example.subastas.data.model.SubastaCreateRequest
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("subastas")
    suspend fun getSubastas(@Query("nombre") nombre: String?): Response<List<Subasta>>

    @GET("subastas/{id}")
    suspend fun getSubastaDetail(@Path("id") id: Int): Response<Subasta>

    @POST("pujas")
    suspend fun realizarPuja(@Body puja: Puja): Response<Subasta>

    @POST("subastas")
    suspend fun crearSubasta(@Body subasta: SubastaCreateRequest): Response<Unit>

    @DELETE("subastas/{id}")
    suspend fun eliminarSubasta(@Path("id") id: Int): Response<Unit>

    @PATCH("subastas/{id}")
    suspend fun finalizarSubasta(@Path("id") id: Int, @Body body: Map<String, String>): Response<Subasta>
}
