package com.example.prayerstimesaap.networking.qibla

import com.example.prayerstimesaap.qibla.QiblaResponse
import retrofit2.Response

interface IQiblaRepo {

    suspend fun getQiblaDirection(latitude: Double, longitude: Double): Response<QiblaResponse>
}