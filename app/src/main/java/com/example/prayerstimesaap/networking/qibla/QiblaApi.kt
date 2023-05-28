package com.example.prayerstimesaap.networking.qibla

import com.example.prayerstimesaap.qibla.QiblaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface QiblaApi {

    @GET("v1/qibla/{latitude}/{longitude}")
    suspend fun getQiblaDirection(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double
    ): Response<QiblaResponse>
}