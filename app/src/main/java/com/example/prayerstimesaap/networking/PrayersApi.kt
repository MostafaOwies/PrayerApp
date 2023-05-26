package com.example.prayerstimesaap.networking

import com.example.prayerstimesaap.prayers.PrayerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface PrayersApi {

    @GET("v1/calendarByCity")
    suspend fun getTimings(
        @Query("date")
        date: String = SimpleDateFormat("dd-mm-yyyy", Locale.getDefault()).format(Date()),
        @Query("country")
        countryCode: String = "eg",
        @Query("methid")
        method: Int = 5,

    ): Response<PrayerResponse>
}