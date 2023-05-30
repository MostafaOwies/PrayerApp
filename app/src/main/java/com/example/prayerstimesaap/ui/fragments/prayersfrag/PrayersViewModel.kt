package com.example.prayerstimesaap.ui.fragments.prayersfrag

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prayerstimesaap.networking.UpcomingPrayersUseCase
import com.example.prayerstimesaap.networking.prayers.PrayersRepo
import com.example.prayerstimesaap.networking.prayers.PrayersUseCase
import com.example.prayerstimesaap.prayer.PrayerResponse
import com.example.prayerstimesaap.prayer.Timings
import com.example.prayerstimesaap.prayer.TimingsResponse
import com.example.prayerstimesaap.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class PrayersViewModel @Inject constructor(
    private val prayersRepo: PrayersRepo,
    private val prayersUseCase: PrayersUseCase,
    private val upcomingPrayersUseCase: UpcomingPrayersUseCase
) : ViewModel(), IPrayersViewModel {

    private val _prayers = MutableStateFlow<Resource<PrayerResponse>>(Resource.Empty())
    val prayers: StateFlow<Resource<PrayerResponse>> = _prayers

    private val _upcomingPrayers = MutableStateFlow<Resource<TimingsResponse>>(Resource.Empty())
    val upcomingPrayers: StateFlow<Resource<TimingsResponse>> = _upcomingPrayers

    init {
        viewModelScope.launch {
            val date: String = SimpleDateFormat("dd-mm-yyyy", Locale.getDefault()).format(Date()).toString()

            val currentDate = LocalDate.now()
            val currentMonth = currentDate.monthValue
            val currentYear = currentDate.year
            val method = 5
            getUpcomingPrayers(date, "Cairo","eg", 5)
            getPrayers(currentYear, currentMonth, "Cairo", "eg", method)
        }
    }


    private fun saveTimings(timings: Timings) =viewModelScope.launch {
        prayersRepo.insertTimings(timings)
    }

    override suspend fun getPrayers(
        year: Int,
        month: Int,
        city: String,
        country: String,
        method:Int
    ) =
        withContext(Dispatchers.Default) {
            try {
                _prayers.value = Resource.Loading()
                _prayers.value = prayersUseCase.handlePrayersResponse(prayersRepo.getPrayers(year, month, city, country, method))
               prayers.collect{ it.data?.data?.forEach {data -> saveTimings(data.timings) } }
            } catch (t: Throwable) {
                throw t
            }
        }

    override suspend fun getUpcomingPrayers(date: String, city:String,countryCode: String, method: Int) =
        withContext(Dispatchers.Default) {

            try {
                _upcomingPrayers.value = Resource.Loading()
                _upcomingPrayers.value = upcomingPrayersUseCase.handlePrayersResponse(prayersRepo.getTimings(date,city, countryCode, method))
            } catch (t: Throwable) {
                throw t
            }
        }
}