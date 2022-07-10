package ru.apmgor.forecast.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import ru.apmgor.forecast.data.forecastprovider.ForecastService
import ru.apmgor.forecast.data.locationprovider.LocationService
import javax.inject.Singleton

private const val API_URL = "http://api.openweathermap.org/"

@Module
@InstallIn(SingletonComponent::class)
object ServiceProviderModule {

    @Provides
    @Singleton
    fun getOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .followSslRedirects(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideService(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(API_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideLocationService(retrofit: Retrofit) = retrofit
        .create<LocationService>()

    @Provides
    @Singleton
    fun provideForecastService(retrofit: Retrofit) = retrofit
        .create<ForecastService>()
}