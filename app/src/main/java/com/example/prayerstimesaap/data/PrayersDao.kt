package com.example.prayerstimesaap.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.prayerstimesaap.prayer.Timings
import kotlinx.coroutines.flow.Flow


@Dao
interface PrayersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrayerTiming(timings: Timings)

    @Query("SELECT * FROM prayer_timings")
    fun getPrayerTimings(): Flow<List<Timings>>
}