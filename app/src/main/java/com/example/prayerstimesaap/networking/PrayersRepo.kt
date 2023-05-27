package com.example.prayerstimesaap.networking

import com.example.prayerstimesaap.prayers.PrayerResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class PrayersRepo @Inject constructor(private val prayersApi: PrayersApi) : IPrayersRepo {


    override suspend fun getPrayers(
        date: String,
        city: String,
        countryCode: String,
        method: Int
    ): Response<PrayerResponse> = withContext(Dispatchers.Default) {
        prayersApi.getTimings(date, city, countryCode, method)
    }

}