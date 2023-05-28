package com.example.prayerstimesaap.ui.fragments.qiblafrag

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
import com.example.prayerstimesaap.databinding.FragmentQiblaFragmnetBinding
import com.example.prayerstimesaap.utils.CompassManager
import com.example.prayerstimesaap.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject
import kotlin.math.roundToInt


@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class QiblaFragmnet : Fragment() {


    @Inject lateinit var compassManager: CompassManager
    private var _binding: FragmentQiblaFragmnetBinding? = null
    private val binding get() = _binding

    private lateinit var viewModel: QiblaViewModel
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

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

        setupCompassRotation()
        Log.d(TAG ,"Qibla ")
        getQibla()

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