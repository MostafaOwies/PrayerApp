package com.example.prayerstimesaap.di.app

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.prayerstimesaap.networking.PrayersApi
import com.example.prayerstimesaap.data.PrayerDB
import com.example.prayerstimesaap.di.RetrofitQ
import com.example.prayerstimesaap.networking.UrlProvider
import com.example.prayerstimesaap.utils.Constants.DATABASE_NAME
import com.example.prayerstimesaap.utils.LocationUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @AppScope
    @RetrofitQ
    fun provideRetrofit(urlProvider: UrlProvider) :Retrofit {
        return  Retrofit.Builder()
            .baseUrl(urlProvider.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @AppScope
    fun provideDataBase(@ApplicationContext context:Context):PrayerDB =
        Room.databaseBuilder(
            context,
            PrayerDB::class.java , DATABASE_NAME
        ).fallbackToDestructiveMigration().build()



    @Provides
    @AppScope
    fun urlProvider()=UrlProvider()

    @Provides
    @AppScope
    fun prayersAPI(@RetrofitQ retrofit: Retrofit) =retrofit.create(PrayersApi::class.java)


    @Provides
    @AppScope
    fun provideLocationUtils(context: Context): LocationUtils {
        return LocationUtils(context)
    }

    @Provides
    @AppScope
    fun provideApplicationContext(application: Application): Context {
        return application.applicationContext
    }

}