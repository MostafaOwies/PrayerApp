package com.example.prayerstimesaap.ui.fragments.prayersfrag

import android.content.ContentValues
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prayerstimesaap.networking.prayers.PrayersRepo
import com.example.prayerstimesaap.networking.prayers.PrayersUseCase
import com.example.prayerstimesaap.prayer.PrayerResponse
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
    private val prayersUseCase: PrayersUseCase
) : ViewModel(), IPrayersViewModel {

    private val _prayers = MutableStateFlow<Resource<PrayerResponse>>(Resource.Empty())
    val prayers: StateFlow<Resource<PrayerResponse>> = _prayers

    init {
        viewModelScope.launch {
            val currentDate = LocalDate.now()
            val currentMonth = currentDate.monthValue
            val currentYear = currentDate.year
            val latitude = 34.4526
            val longitude = 19.6866
            val method = 5

            getPrayers(currentYear, currentMonth, longitude, latitude, method)
        }
    }


    override suspend fun getPrayers(
        year: Int,
        month: Int,
        latitude: Double,
        longitude: Double,
        method: Int
    ) =
        withContext(Dispatchers.Default) {
            try {
                _prayers.value = Resource.Loading()
                _prayers.value = prayersUseCase.handlePrayersResponse(
                    prayersRepo.getPrayers(
                        year,
                        month,
                        latitude,
                        longitude,
                        method
                    )
                )
            } catch (t: Throwable) {
                throw t
            }
        }
}