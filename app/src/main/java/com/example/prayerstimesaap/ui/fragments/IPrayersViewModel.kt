package com.example.prayerstimesaap.ui.fragments

interface IPrayersViewModel {
    suspend fun getPrayers(date: String, countryCode: String, method: Int)
}