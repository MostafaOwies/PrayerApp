package com.example.prayerstimesaap.networking

import com.example.prayerstimesaap.prayers.PrayerResponse
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
                    response.body()?.let {
                        if (prayersResponse == null) {
                            prayersResponse = it
                        } else {
                            val oldResponse = prayersResponse?.data
                            val newResponse = it.data

                        }
                        return@withContext Resource.Success(prayersResponse ?: it)
                    }
                }
                return@withContext Resource.Error(response.message(), null)
            } catch (t: Throwable) {
                if (t !is CancellationException) {
                    return@withContext Resource.Error(response.message(), null)
                } else {
                    throw t
                }
            }
        }
    }
}