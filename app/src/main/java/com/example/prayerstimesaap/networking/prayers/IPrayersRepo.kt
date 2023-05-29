package com.example.prayerstimesaap.networking.prayers

import com.example.prayerstimesaap.prayer.PrayerResponse
import retrofit2.Response

interface IPrayersRepo {

    suspend fun getPrayers(
        year: Int,
        month: Int,
        latitude: Double,
        longitude: Double,
        method :Int
    ): Response<PrayerResponse>
}