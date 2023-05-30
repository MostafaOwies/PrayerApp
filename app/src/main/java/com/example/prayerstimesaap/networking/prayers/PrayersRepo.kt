package com.example.prayerstimesaap.networking.prayers


import com.example.prayerstimesaap.data.PrayersDao
import com.example.prayerstimesaap.prayer.PrayerResponse
import com.example.prayerstimesaap.prayer.Timings
import com.example.prayerstimesaap.prayer.TimingsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class PrayersRepo @Inject constructor(
    private val prayersApi: PrayersApi,
    private val prayerTimingDao: PrayersDao

) : IPrayersRepo {


    override suspend fun getPrayers(
        year: Int,
        month: Int,
        city: String,
        country: String,
        method:Int
    ): Response<PrayerResponse> = withContext(Dispatchers.Default) {
        prayersApi.getTimings(year, month, city, country,method)
    }

    override suspend fun getTimings(
        date: String,
        city: String,
        countryCode: String,
        method: Int
    ): Response<TimingsResponse> = withContext(Dispatchers.Default) {
        prayersApi.getUpcomingTimings(date, city, countryCode, method)
    }

     suspend fun insertTimings(timings: Timings) {
        prayerTimingDao.insertPrayerTiming(timings)
    }
    fun getPrayerTimings(): Flow<List<Timings>> {
        return prayerTimingDao.getPrayerTimings()
    }

}