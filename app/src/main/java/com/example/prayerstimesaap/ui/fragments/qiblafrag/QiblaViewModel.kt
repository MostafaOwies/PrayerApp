package com.example.prayerstimesaap.ui.fragments.qiblafrag

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prayerstimesaap.networking.qibla.QiblaRepo
import com.example.prayerstimesaap.networking.qibla.QiblaUseCase
import com.example.prayerstimesaap.qibla.QiblaResponse
import com.example.prayerstimesaap.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
@SuppressLint("StaticFieldLeak")
class QiblaViewModel @Inject constructor(
    private val qiblaRepo: QiblaRepo,
    private val qiblaUseCase: QiblaUseCase,
    @ApplicationContext private val context: Context

) :ViewModel(),IQiblaViewModel{

    private val _qiblaDirection = MutableStateFlow<Resource<QiblaResponse>>(Resource.Empty())
    val qiblaDirection: StateFlow<Resource<QiblaResponse>> = _qiblaDirection

    init {
        viewModelScope.launch {
            fetchQiblaDirection(34.4526,19.6866)
        }
    }




    override suspend fun fetchQiblaDirection(latitude: Double, longitude: Double) {
        withContext(Dispatchers.Default) {
            try {
                if (isNetworkConnected(context)) {
                    _qiblaDirection.value = Resource.Loading()
                    _qiblaDirection.value = qiblaUseCase.handleQiblaResponse(
                        qiblaRepo.getQiblaDirection(
                            latitude,
                            longitude
                        )
                    )

                }  else {
                        _qiblaDirection.value = Resource.Error("No internet connection")
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

     override fun isNetworkConnected(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            ?: return false

        return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

}