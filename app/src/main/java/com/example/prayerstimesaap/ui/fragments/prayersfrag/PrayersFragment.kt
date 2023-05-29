package com.example.prayerstimesaap.ui.fragments.prayersfrag

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prayerstimesaap.MainActivity
import com.example.prayerstimesaap.adapters.PrayersAdapter
import com.example.prayerstimesaap.databinding.FragmentPrayersBinding
import com.example.prayerstimesaap.prayer.PrayerResponse
import com.example.prayerstimesaap.utils.Constants
import com.example.prayerstimesaap.utils.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.log


@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("MissingPermission")
class PrayersFragment : Fragment() {

    private var _binding: FragmentPrayersBinding? = null
    private val binding get() = _binding

    private lateinit var viewModel: PrayersViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var adapter: PrayersAdapter


    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideActionBar()
        _binding = FragmentPrayersBinding.inflate(inflater, container, false)
        return binding?.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).prayersViewModel
        setUpRecyclerView()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        checkLocationPermission()

        getPrayers()


    }

    //Check for Location Permission
    private fun checkLocationPermission() {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } == PackageManager.PERMISSION_GRANTED) {
            getLocation()
        } else {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    Constants.LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    //Handle Location
    private fun getLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    coroutineScope.launch {
                        try {
                            val addresses =
                                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                            val country = addresses?.let { getCountryName(it) }
                            binding?.locationLayout?.locationTv?.text = country
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
    }

    private fun getCountryName(addresses: List<Address>): String? {
        if (addresses.isNotEmpty()) {
            val address = addresses[0]
            val country = address.countryName
            val city = address.subAdminArea
            return "$country,  $city"
        }
        return null
    }

    //Fetch prayers from API
    private fun getPrayers() {
        coroutineScope.launch {
            try {
                viewModel.prayers.collect { response ->
                    when (response) {
                        is Resource.Success -> {
                            response.data.let {
                                adapter.difference.submitList(it?.data)

                                /* binding?.prayerLayout?.fajrTimer?.text =
                                                            formatTime(it?.data?.timings?.Fajr)
                                                        binding?.prayerLayout?.dhuhrTimer?.text =
                                                            formatTime(it?.data?.timings?.Dhuhr)
                                                        binding?.prayerLayout?.asrTimer?.text =
                                                            formatTime(it?.data?.timings?.Asr)
                                                        binding?.prayerLayout?.maghribTimer?.text =
                                                            formatTime(it?.data?.timings?.Maghrib)
                                                        binding?.prayerLayout?.ishaTimer?.text =
                                                            formatTime(it?.data?.timings?.Isha)
*/



                                                        //getCountDown()


                            }
                        }

                        is Resource.Error -> {
                            Log.d(TAG, "Prayers failed${response.message}")
                        }

                        is Resource.Loading -> {
                            Log.d(TAG, "Prayers loading")
                        }

                        else -> {}
                    }
                }
            } catch (e: Exception) {
                e.stackTrace
            }
        }
    }

    //Setup recycler View
    private fun setUpRecyclerView(){
        adapter= PrayersAdapter(this)
        binding?.prayerLayout?.rvPrayers?.adapter=adapter
        binding?.prayerLayout?.rvPrayers?.layoutManager= LinearLayoutManager(activity)
        binding?.prayerLayout?.rvPrayers?.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

    }

    //Setup the count down for the upcoming prayer
    private fun getCountDown() {
        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()

        val upcomingPrayer = adapter.prayerTimings
            ?.map { LocalTime.of(it.hour, it.minute) } // Convert prayer times to LocalTime
            ?.filter { time ->
                LocalDateTime.of(currentDate, time) > LocalDateTime.of(
                    currentDate,
                    currentTime
                )
            }
            ?.minOrNull() ?: adapter.prayerTimings?.first()

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
                val prayerName = getUpcomingPrayerName(upcomingPrayer)

                binding?.upcomingPrayerLayout?.nextPrayerTimerTv?.text = formattedDuration
                binding?.upcomingPrayerLayout?.nextPrayerTv?.text = prayerName
                delay(1000)
            }
        }
    }


    //format the remaining duration in hh:mm:ss format
    private fun formatDuration(duration: Duration): String {
        val seconds = duration.seconds % 60
        val minutes = (duration.seconds % 3600) / 60
        val hours = duration.seconds / 3600
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }


    // allocate the prayer name according to the upcoming prayer
    private fun getUpcomingPrayerName(prayerTime: LocalTime?): String {
        return when (prayerTime) {
            adapter.prayerTimings?.get(0) -> "Fajr"
            adapter.prayerTimings?.get(1) -> "Dhuhr"
            adapter.prayerTimings?.get(2) -> "Asr"
            adapter.prayerTimings?.get(3) -> "Maghrib"
            adapter.prayerTimings?.get(4) -> "Isha"
            else -> ""
        }
    }

    //format prayers timing in 12h format
    private fun formatTime(time: String?): String {
        time?.let {
            val localTime = LocalTime.parse(it)
            return localTime.format(DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH))
        }
        return ""
    }

    private fun hideActionBar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
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