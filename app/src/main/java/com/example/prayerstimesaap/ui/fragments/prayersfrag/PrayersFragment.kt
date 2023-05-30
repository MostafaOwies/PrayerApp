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
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.prayerstimesaap.MainActivity
import com.example.prayerstimesaap.adapters.PrayersAdapter
import com.example.prayerstimesaap.databinding.FragmentPrayersBinding
import com.example.prayerstimesaap.prayer.Data
import com.example.prayerstimesaap.prayer.TimingsResponse
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


@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("MissingPermission")
class PrayersFragment : Fragment() {

    private var _binding: FragmentPrayersBinding? = null
    private val binding get() = _binding

    private lateinit var viewModel: PrayersViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var adapter: PrayersAdapter

    var prayersResponse: TimingsResponse? = null
    var prayerTimings: List<LocalTime>? = null

    var currentDate = LocalDate.now()
    var filteredPrayers :List<Data>?= null

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

        getUpcomingTimings()

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
                            val city = addresses?.let { getCityOnly(it) }
                            binding?.locationLayout?.locationTv?.text = country
                            val currentDate = LocalDate.now()
                            val currentMonth = currentDate.monthValue
                            val currentYear = currentDate.year
                            val method = 5
                            viewModel.getPrayers(currentYear, currentMonth, city!!, "eg", method)
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
    private fun getCityOnly(addresses: List<Address>): String? {
        if (addresses.isNotEmpty()) {
            val address = addresses[0]
            return address.subAdminArea
        }
        return null
    }


    private fun getUpcomingTimings() {
        coroutineScope.launch {
            try {
                viewModel.upcomingPrayers.collect { response ->
                    when (response) {
                        is Resource.Success -> {
                            response.data.let {
                                prayersResponse = it
                                prayerTimings = prayersResponse?.data?.timings?.toLocalTimeList()
                                getCountDown()
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

    //Fetch prayers from API
    private fun getPrayers() {
        coroutineScope.launch {
            try {
                viewModel.prayers.collect { response ->
                    when (response) {
                        is Resource.Success -> {
                            response.data.let {
                                Log.d(TAG, "Prayers failed${it?.data}")
                                filteredPrayers = it?.data?.filter { prayer ->
                                    val prayerDate = LocalDate.parse(prayer.date.gregorian.date, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                                    prayerDate >= currentDate
                                }

                                adapter.difference.submitList(filteredPrayers)
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
    private fun setUpRecyclerView() {
        adapter = PrayersAdapter()
        binding?.prayerLayout?.rvPrayers?.adapter = adapter
        binding?.prayerLayout?.rvPrayers?.layoutManager = LinearLayoutManager(activity)
        binding?.prayerLayout?.rvPrayers?.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val pagerSnapHelper = PagerSnapHelper()
        pagerSnapHelper.attachToRecyclerView(binding?.prayerLayout?.rvPrayers)
        binding?.prayerLayout?.viewPager?.adapter = PrayersAdapter()

        binding?.prayerLayout?.nextDay?.setOnClickListener {
           adapter.scrollToNextItem( binding!!.prayerLayout.rvPrayers)
        }

        binding?.prayerLayout?.prevDay?.setOnClickListener {
            adapter.scrollToPreviousItem( binding!!.prayerLayout.rvPrayers)
        }
    }


    //Setup the count down for the upcoming prayer
    private fun getCountDown() {
        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()

        val upcomingPrayer = prayerTimings
            ?.map { LocalTime.of(it.hour, it.minute) } // Convert prayer times to LocalTime
            ?.filter { time ->
                LocalDateTime.of(currentDate, time) > LocalDateTime.of(
                    currentDate,
                    currentTime
                )
            }
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
            prayerTimings?.get(0) -> "Fajr"
            prayerTimings?.get(1) -> "Dhuhr"
            prayerTimings?.get(2) -> "Asr"
            prayerTimings?.get(3) -> "Maghrib"
            prayerTimings?.get(4) -> "Isha"
            else -> ""
        }
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