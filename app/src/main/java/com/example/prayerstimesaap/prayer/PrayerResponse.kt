package com.example.prayerstimesaap.prayer

data class PrayerResponse(
    val code: Int,
    val data: List<Data>,
    val status: String
)