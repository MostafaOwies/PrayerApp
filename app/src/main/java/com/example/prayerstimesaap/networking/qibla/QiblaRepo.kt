package com.example.prayerstimesaap.networking.qibla

import com.example.prayerstimesaap.qibla.QiblaResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class QiblaRepo @Inject constructor(private val qiblaApi: QiblaApi) : IQiblaRepo {

    override suspend fun getQiblaDirection(
        latitude: Double,
        longitude: Double
    ): Response<QiblaResponse> = withContext(Dispatchers.Default) {
            qiblaApi.getQiblaDirection(latitude, longitude)
        }
}