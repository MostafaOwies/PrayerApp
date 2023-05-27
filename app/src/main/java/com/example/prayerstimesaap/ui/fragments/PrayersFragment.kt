package com.example.prayerstimesaap.ui.fragments

import android.content.ContentValues.TAG
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.prayerstimesaap.MainActivity
import com.example.prayerstimesaap.databinding.FragmentPrayersBinding
import com.example.prayerstimesaap.prayers.PrayerResponse
import com.example.prayerstimesaap.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.internal.concurrent.formatDuration
import okhttp3.internal.format
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date
import java.util.Locale
import kotlin.math.log


@AndroidEntryPoint
class PrayersFragment : Fragment() {

    private var _binding: FragmentPrayersBinding? = null

    private val binding get() = _binding
    lateinit var viewModel: PrayersViewModel
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    private var prayersResponse: PrayerResponse? = null
    private var prayerTimings: List<LocalTime>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPrayersBinding.inflate(inflater, container, false)
        return binding?.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel


        getPrayers()



    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPrayers() {
        coroutineScope.launch {
            try {
                viewModel.prayers.collect { response ->
                    when (response) {
                        is Resource.Success -> {
                            response.data.let {
                                binding?.prayerLayout?.fajrTimer?.text = it?.data?.timings?.Fajr
                                binding?.prayerLayout?.dhuhrTimer?.text = it?.data?.timings?.Dhuhr
                                binding?.prayerLayout?.asrTimer?.text = it?.data?.timings?.Asr
                                binding?.prayerLayout?.maghribTimer?.text = it?.data?.timings?.Maghrib
                                binding?.prayerLayout?.ishaTimer?.text = it?.data?.timings?.Isha

                                prayersResponse = it
                                prayerTimings = prayersResponse?.data?.timings?.toLocalTimeList()
                                Log.d(TAG,"aaaaaaaaaaaaa${prayerTimings.toString()}" )
                                Log.d(TAG,"Prayers${it?.data?.timings}")


                                getCountDown()

                            }


                        }

                        is Resource.Error -> {
                            //hideProgressBar()

                            Log.d(TAG, "Prayers failed${response.message}")
                        }

                        is Resource.Loading -> {
                            Log.d(TAG, "Prayers loading")
                            //showProgressBar()
                        }

                        else -> {}
                    }
                }

            } catch (e: Exception) {
                e.stackTrace
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCountDown() {
        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()

        val upcomingPrayer = prayerTimings
            ?.map { LocalTime.of(it.hour, it.minute) } // Convert prayer times to LocalTime
            ?.filter { time -> LocalDateTime.of(currentDate, time) > LocalDateTime.of(currentDate, currentTime) }
            ?.minOrNull() ?: prayerTimings?.first()

        val prayerTimeForNextDay = upcomingPrayer?.isBefore(currentTime)

        coroutineScope.launch {
            while (isActive) {
                val targetDateTime = if (prayerTimeForNextDay == true) {
                    LocalDateTime.of(currentDate.plusDays(1), upcomingPrayer)
                } else {
                    LocalDateTime.of(currentDate, upcomingPrayer)
                }

                val remainingTime = Duration.between(LocalDateTime.now(), targetDateTime)
                val formattedDuration = formatDuration(remainingTime)

                binding?.upcomingPrayerLayout?.nextPrayerTimerTv?.text = formattedDuration
                delay(1000)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatDuration(duration: Duration): String {
        val seconds = duration.seconds % 60
        val minutes = (duration.seconds % 3600) / 60
        val hours = duration.seconds / 3600
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStop() {
        super.onStop()
        coroutineScope.coroutineContext.cancelChildren()
    }
}