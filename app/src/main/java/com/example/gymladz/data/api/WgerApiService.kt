package com.example.gymladz.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface WgerApiService {
    
    @GET("exercise/")
    suspend fun getExercises(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("language") language: Int = 2 // English
    ): ExerciseResponse
    
    @GET("exerciseinfo/")
    suspend fun getExerciseInfo(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("language") language: Int = 2 // English
    ): ExerciseInfoResponse
    
    @GET("exercisecategory/")
    suspend fun getCategories(): CategoryResponse
}
