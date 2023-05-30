package com.example.prayerstimesaap.ui.fragments.prayersfrag

interface IPrayersViewModel {
    suspend fun getPrayers(
        year: Int,
        month: Int,
        city: String,
        country: String,
        method:Int
    )

    suspend fun getUpcomingPrayers(date: String, city: String, countryCode: String, method: Int)

}