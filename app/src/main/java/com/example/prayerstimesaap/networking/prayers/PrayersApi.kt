package com.example.prayerstimesaap.networking.prayers

import com.example.prayerstimesaap.prayer.PrayerResponse
import com.example.prayerstimesaap.prayer.TimingsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PrayersApi {

    @GET("calendar/{year}/{month}")
    suspend fun getTimings(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int

    ): Response<PrayerResponse>

    @GET("v1/timingsByCity/{date}")
    suspend fun getUpcomingTimings(
        @Path("date") date: String,
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: Int

    ): Response<TimingsResponse>
}