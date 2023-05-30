package com.example.prayerstimesaap.networking

import android.content.ContentValues
import android.util.Log
import com.example.prayerstimesaap.prayer.TimingsResponse
import com.example.prayerstimesaap.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.util.concurrent.CancellationException
import javax.inject.Inject

class UpcomingPrayersUseCase @Inject constructor() {

    private var timingsResponse: TimingsResponse? = null

    suspend fun handlePrayersResponse(response: Response<TimingsResponse>): Resource<TimingsResponse> {
        return withContext(Dispatchers.Default) {
            try {
                if (response.isSuccessful) {
                    Log.d(ContentValues.TAG, "Prayers")
                    response.body()?.let {
                        timingsResponse = it
                        return@withContext Resource.Success(timingsResponse ?: it)
                    }
                }
                Log.d(ContentValues.TAG, "Prayers Error")
                return@withContext Resource.Error(response.message(), null)
            } catch (t: Throwable) {
                if (t !is CancellationException) {
                    Log.d(ContentValues.TAG, "Prayers Throwable")
                    return@withContext Resource.Error(response.message(), null)
                } else {
                    throw t
                }
            }
        }
    }
}