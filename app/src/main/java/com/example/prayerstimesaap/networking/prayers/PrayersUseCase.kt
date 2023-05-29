package com.example.prayerstimesaap.networking.prayers

import android.content.ContentValues
import android.util.Log
import com.example.prayerstimesaap.prayer.PrayerResponse
import com.example.prayerstimesaap.utils.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject


class PrayersUseCase @Inject constructor() {

    private var prayersResponse: PrayerResponse? = null

    suspend fun handlePrayersResponse(response: Response<PrayerResponse>): Resource<PrayerResponse> {
        return withContext(Dispatchers.Default) {
            try {
                if (response.isSuccessful) {
                    Log.d(ContentValues.TAG, "Prayers")
                    response.body()?.let {
                        prayersResponse = it
                        return@withContext Resource.Success(prayersResponse ?: it)
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