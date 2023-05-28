package com.example.prayerstimesaap.ui.fragments.prayersfrag

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prayerstimesaap.networking.prayers.PrayersRepo
import com.example.prayerstimesaap.networking.prayers.PrayersUseCase
import com.example.prayerstimesaap.prayers.PrayerResponse
import com.example.prayerstimesaap.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PrayersViewModel @Inject constructor(
    private val prayersRepo: PrayersRepo,
    private val prayersUseCase: PrayersUseCase
) : ViewModel(), IPrayersViewModel {

    private val _prayers = MutableStateFlow<Resource<PrayerResponse>>(Resource.Empty())
    val prayers: StateFlow<Resource<PrayerResponse>> = _prayers

    init {
        viewModelScope.launch {
            val date: String = SimpleDateFormat("dd-mm-yyyy", Locale.getDefault()).format(Date()).toString()
            Log.d(ContentValues.TAG,date)

            getPrayers(date, "Cairo","eg", 5)
        }
    }



    override suspend fun getPrayers(date: String, city:String,countryCode: String, method: Int) =
        withContext(Dispatchers.Default) {

            try {
                _prayers.value = Resource.Loading()
                _prayers.value = prayersUseCase.handlePrayersResponse(prayersRepo.getPrayers(date,city, countryCode, method))
            } catch (t: Throwable) {
                throw t
            }
        }
}