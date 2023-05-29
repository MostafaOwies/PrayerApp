package com.example.prayerstimesaap.prayer

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class Timings(
    val Asr: String,
    val Dhuhr: String,
    val Fajr: String,
    val Isha: String,
    val Maghrib: String,
){
    @RequiresApi(Build.VERSION_CODES.O)
    fun toLocalTimeList(): List<LocalTime> {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return listOf(
            LocalTime.parse(Fajr.replaceFirst(" \\(.*\\)".toRegex(), ""), formatter),
            LocalTime.parse(Dhuhr.replaceFirst(" \\(.*\\)".toRegex(), ""), formatter),
            LocalTime.parse(Asr.replaceFirst(" \\(.*\\)".toRegex(), ""), formatter),
            LocalTime.parse(Maghrib.replaceFirst(" \\(.*\\)".toRegex(), ""), formatter),
            LocalTime.parse(Isha.replaceFirst(" \\(.*\\)".toRegex(), ""), formatter)
        )
    }
}