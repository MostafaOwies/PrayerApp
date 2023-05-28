package com.example.prayerstimesaap.ui.fragments.qiblafrag

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
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
import com.example.prayerstimesaap.MainActivity
import com.example.prayerstimesaap.databinding.FragmentQiblaFragmnetBinding
import com.example.prayerstimesaap.utils.CompassManager
import com.example.prayerstimesaap.utils.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.Locale
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("MissingPermission")
class QiblaFragmnet : Fragment() {


    @Inject lateinit var compassManager: CompassManager
    private var _binding: FragmentQiblaFragmnetBinding? = null
    private val binding get() = _binding

    private lateinit var viewModel: QiblaViewModel
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentQiblaFragmnetBinding.inflate(inflater, container, false)
        return binding?.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).qiblaViewModel
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setupCompassRotation()
        Log.d(TAG ,"Qibla ")
        getQibla()
        getLocation()

        compassManager.start()

    }

    private fun getQibla() {
        coroutineScope.launch{
            try {
             viewModel.qiblaDirection.collect{response->
                    when(response) {
                        is Resource.Success -> {
                            response.data.let {
                                Log.d(TAG ,"Qibla ${it?.data}")
                                val intDirection= it?.data?.direction?.toInt()
                                binding?.directionTtv?.text=intDirection.toString()+"\u00B0"
                            }
                        }
                        is Resource.Error -> {
                            Log.d(TAG, "Qibla failed${response.message}")
                        }

                        is Resource.Loading -> {
                            Log.d(TAG, "Qibla loading")
                        }

                        else -> {}
                    }
             }
            }catch (e:Exception) {
                e.stackTrace
            }
        }
    }

    private fun getLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    coroutineScope.launch {
                        try {
                            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                            val country = addresses?.let { getCountryName(it) }
                            binding?.locationTtv?.text = country
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

            return address.subAdminArea
        }
        return null
    }

    private fun setupCompassRotation() {
        compassManager.onAzimuthChangedListener = { azimuth ->
            binding?.compassImageview?.rotation = -azimuth.roundToInt().toFloat()
        }
    }

    override fun onResume() {
        super.onResume()
        compassManager.start()
    }

    override fun onPause() {
        super.onPause()
        compassManager.stop()
    }


    override fun onDestroyView() {
        compassManager.stop()
        _binding = null
        super.onDestroyView()
    }


}