package com.example.prayerstimesaap.ui.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.prayerstimesaap.MainActivity
import com.example.prayerstimesaap.databinding.FragmentPrayersBinding
import com.example.prayerstimesaap.utils.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import java.lang.Exception


@AndroidEntryPoint
class PrayersFragment : Fragment() {

    private var _binding: FragmentPrayersBinding? = null

    private val binding get() = _binding
    lateinit var viewModel: PrayersViewModel
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel=(activity as MainActivity).viewModel
        getPrayers ()
    }


    private fun getPrayers () {
        coroutineScope.launch {
            try {
                viewModel.prayers.collect{response ->
                    when(response){
                        is Resource.Success -> {
                            response.data.let {

                                Log.d(TAG,"Prayers${it?.data?.timings}")
                                Log.d(TAG,"Prayers")
                            }

                        }

                        is Resource.Error -> {
                            //hideProgressBar()

                            Log.d(TAG,"Prayers failed${response.message}")
                        }
                        is Resource.Loading -> {
                            Log.d(TAG,"Prayers loading")
                            //showProgressBar()
                        }
                        else -> {}
                    }
                }

            }catch ( e:Exception) {
                e.stackTrace
            }
        }
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