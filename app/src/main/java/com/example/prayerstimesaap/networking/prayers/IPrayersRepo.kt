package com.example.prayerstimesaap.networking.prayers

import com.example.prayerstimesaap.prayer.PrayerResponse
import com.example.prayerstimesaap.prayer.TimingsResponse
import retrofit2.Response

interface IPrayersRepo {

    suspend fun getPrayers(
        year: Int,
        month: Int,
        latitude: Double,
        longitude: Double,
        method :Int
    ): Response<PrayerResponse>

    suspend fun getTimings(
        date: String,
        city: String,
        countryCode: String,
        method: Int
    ): Response<TimingsResponse>
}