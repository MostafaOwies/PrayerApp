package com.example.prayerstimesaap.networking.prayers


import com.example.prayerstimesaap.prayer.PrayerResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class PrayersRepo @Inject constructor(
    private val prayersApi: PrayersApi,
) : IPrayersRepo {


    override suspend fun getPrayers(
        year: Int,
        month: Int,
        latitude: Double,
        longitude: Double,
        method:Int
    ): Response<PrayerResponse> = withContext(Dispatchers.Default) {
        prayersApi.getTimings(year, month, latitude, longitude,method)
    }

}