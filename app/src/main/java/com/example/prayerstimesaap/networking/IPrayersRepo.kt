package com.example.prayerstimesaap.networking

import com.example.prayerstimesaap.prayers.PrayerResponse
import retrofit2.Response

interface IPrayersRepo {

    suspend fun getPrayers(date:String , countryCode :String , method :Int) :Response<PrayerResponse>
}