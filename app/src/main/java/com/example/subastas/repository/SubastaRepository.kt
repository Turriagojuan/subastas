package com.example.subastas.repository

import com.example.subastas.data.model.Puja
import com.example.subastas.data.model.Subasta
import com.example.subastas.data.model.SubastaCreateRequest
import com.example.subastas.data.remote.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubastaRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getSubastas(query: String?): Result<List<Subasta>> {
        return try {
            val response = apiService.getSubastas(query)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar subastas: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSubastaDetail(id: Int): Result<Subasta> {
        return try {
            val response = apiService.getSubastaDetail(id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al cargar detalle: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun realizarPuja(puja: Puja): Result<Subasta> {
        return try {
            val response = apiService.realizarPuja(puja)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al realizar puja: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearSubasta(subasta: SubastaCreateRequest): Result<Subasta> {
        return try {
            val response = apiService.crearSubasta(subasta)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al crear subasta: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarSubasta(id: Int): Result<Unit> {
        return try {
            val response = apiService.eliminarSubasta(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar subasta: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun finalizarSubasta(id: Int): Result<Subasta> {
        return try {
            val response = apiService.finalizarSubasta(id, mapOf("estado" to "Cerrada"))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al finalizar subasta: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
