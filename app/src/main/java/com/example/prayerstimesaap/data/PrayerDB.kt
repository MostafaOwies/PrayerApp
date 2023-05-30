package com.example.prayerstimesaap.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.prayerstimesaap.prayer.Timings

@Database(
    entities = [
        PrayersEntity::class,
        Timings::class
    ], version = 2
)
abstract class PrayerDB : RoomDatabase() {

    abstract fun getPrayersDao(): PrayersDao

}