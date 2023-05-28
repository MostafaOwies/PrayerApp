package com.example.prayerstimesaap.ui.fragments.qiblafrag

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prayerstimesaap.networking.qibla.QiblaRepo
import com.example.prayerstimesaap.networking.qibla.QiblaUseCase
import com.example.prayerstimesaap.qibla.QiblaResponse
import com.example.prayerstimesaap.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class QiblaViewModel @Inject constructor(
    private val qiblaRepo: QiblaRepo,
    private val qiblaUseCase: QiblaUseCase
    ) :ViewModel(){

    private val _qiblaDirection = MutableStateFlow<Resource<QiblaResponse>>(Resource.Empty())
    val qiblaDirection: StateFlow<Resource<QiblaResponse>> = _qiblaDirection

    init {
        viewModelScope.launch {
            fetchQiblaDirection(34.4526,19.6866)
        }
    }




    suspend fun fetchQiblaDirection(latitude: Double, longitude: Double) {
        withContext(Dispatchers.Default) {
            try {
                _qiblaDirection.value = Resource.Loading()
                _qiblaDirection.value= qiblaUseCase.handleQiblaResponse(qiblaRepo.getQiblaDirection(latitude,longitude))

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}