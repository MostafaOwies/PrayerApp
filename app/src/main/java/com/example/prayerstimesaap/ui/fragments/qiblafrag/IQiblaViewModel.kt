package com.example.prayerstimesaap.ui.fragments.qiblafrag

import android.content.Context

interface IQiblaViewModel {
     suspend fun fetchQiblaDirection(latitude: Double, longitude: Double)
     fun isNetworkConnected(context: Context): Boolean
}