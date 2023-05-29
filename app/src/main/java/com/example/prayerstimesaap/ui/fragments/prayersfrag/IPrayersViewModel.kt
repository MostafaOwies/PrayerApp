package com.example.prayerstimesaap.ui.fragments.prayersfrag

interface IPrayersViewModel {
    suspend fun getPrayers(
        year: Int,
        month: Int,
        latitude: Double,
        longitude: Double,
        method: Int
    )

    suspend fun getUpcomingPrayers(date: String, city: String, countryCode: String, method: Int)

}