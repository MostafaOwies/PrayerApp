package com.example.prayerstimesaap.prayer

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class Data(
    val date: Date,
    val meta: Meta,
    val timings: Timings
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun toLocalTimeList(): List<LocalTime> {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return listOf(
            LocalTime.parse(timings.Fajr.replaceFirst(" \\(.*\\)".toRegex(), ""), formatter),
            LocalTime.parse(timings.Dhuhr.replaceFirst(" \\(.*\\)".toRegex(), ""), formatter),
            LocalTime.parse(timings.Asr.replaceFirst(" \\(.*\\)".toRegex(), ""), formatter),
            LocalTime.parse(timings.Maghrib.replaceFirst(" \\(.*\\)".toRegex(), ""), formatter),
            LocalTime.parse(timings.Isha.replaceFirst(" \\(.*\\)".toRegex(), ""), formatter)
        )
    }
}