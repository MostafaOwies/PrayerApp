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
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.Duration
import java.time.LocalTime


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
        var currentTime = LocalTime.now()
        var upComingPrayer :LocalTime?= null
        for (prayerTiming in prayerTimings!!) {
            if(prayerTiming > currentTime) {
                upComingPrayer=prayerTiming
                break
            }
        }

        val durationUntilPrayer = Duration.between(currentTime, upComingPrayer)
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