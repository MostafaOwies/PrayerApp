package com.example.prayerstimesaap.networking.qibla

import android.content.ContentValues
import android.util.Log
import com.example.prayerstimesaap.qibla.QiblaResponse
import com.example.prayerstimesaap.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class QiblaUseCase @Inject constructor() {

    private var qiblaResponse: QiblaResponse? = null

    suspend fun handleQiblaResponse(response: Response<QiblaResponse>): Resource<QiblaResponse> {
        return withContext(Dispatchers.Default) {
            try {
                if (response.isSuccessful) {
                    Log.d(ContentValues.TAG, "Qibla")
                    response.body()?.let {
                        qiblaResponse = it
                        return@withContext Resource.Success(qiblaResponse ?: it)
                    }
                }
                Log.d(ContentValues.TAG, "Qibla Error")
                return@withContext Resource.Error(response.message(), null)
            } catch (t: Throwable) {
                if (t !is CancellationException) {
                    Log.d(ContentValues.TAG, "Qibla Throwable")
                    return@withContext Resource.Error(response.message(), null)
                } else {
                    throw t
                }
            }
        }
    }
}