package com.example.prayerstimesaap.ui.fragments.prayersfrag

interface IPrayersViewModel {
    suspend fun getPrayers(
        year: Int,
        month: Int,
        latitude: Double,
        longitude: Double,
        method:Int
    )
}