package com.example.prayerstimesaap.prayers

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class Timings(
    val Asr: String,
    val Dhuhr: String,
    val Fajr: String,
    val Firstthird: String,
    val Imsak: String,
    val Isha: String,
    val Lastthird: String,
    val Maghrib: String,
    val Midnight: String,
    val Sunrise: String,
    val Sunset: String
)
{

    @RequiresApi(Build.VERSION_CODES.O)
    fun toLocalTimeList(): List<LocalTime> {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return listOf(
            LocalTime.parse(Fajr, formatter),
            LocalTime.parse(Dhuhr, formatter),
            LocalTime.parse(Asr, formatter),
            LocalTime.parse(Maghrib, formatter),
            LocalTime.parse(Isha, formatter)
        )
    }
}