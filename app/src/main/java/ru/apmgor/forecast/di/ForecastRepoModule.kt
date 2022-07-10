package ru.apmgor.forecast.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import ru.apmgor.forecast.data.ForecastRepo
import ru.apmgor.forecast.data.ForecastRepoImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ForecastRepoModule {

    @Binds
    @Singleton
    abstract fun bindForecastRepo(repoImpl: ForecastRepoImpl): ForecastRepo
}

@Module
@InstallIn(SingletonComponent::class)
object ForecastRepoProvideModule {

    @Provides
    @Singleton
    fun provideDispatcher() = Dispatchers.IO
}
