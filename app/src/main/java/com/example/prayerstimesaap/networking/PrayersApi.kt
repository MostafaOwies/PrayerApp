package com.example.prayerstimesaap.networking

import com.example.prayerstimesaap.prayers.PrayerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface PrayersApi {

    @GET("v1/timingsByCity/{date}")
    suspend fun getTimings(
        @Path("date") date: String,
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: Int

        ): Response<PrayerResponse>
}