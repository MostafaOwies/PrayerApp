package com.example.prayerstimesaap.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PrayersEntity::class], version = 1)
abstract class PrayerDB :RoomDatabase() {
}