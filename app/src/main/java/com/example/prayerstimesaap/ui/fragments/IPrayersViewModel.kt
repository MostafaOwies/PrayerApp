package com.example.prayerstimesaap.ui.fragments

interface IPrayersViewModel {
    suspend fun getPrayers(date: String, city: String, countryCode: String, method: Int)
}