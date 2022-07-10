package ru.apmgor.forecast.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.apmgor.forecast.data.forecastprovider.ForecastProvider
import ru.apmgor.forecast.data.forecastprovider.ForecastProviderImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ForecastProviderModule {

    @Binds
    @Singleton
    abstract fun bindForecastProvider(locImpl: ForecastProviderImpl): ForecastProvider
}