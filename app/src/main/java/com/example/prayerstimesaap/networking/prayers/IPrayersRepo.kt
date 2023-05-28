package com.example.prayerstimesaap.networking.prayers

import com.example.prayerstimesaap.prayers.PrayerResponse
import retrofit2.Response

interface IPrayersRepo {

    suspend fun getPrayers(
        date: String,
        city: String,
        countryCode: String,
        method: Int
    ): Response<PrayerResponse>
}