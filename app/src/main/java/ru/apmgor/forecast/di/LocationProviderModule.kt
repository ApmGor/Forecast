package ru.apmgor.forecast.di

import android.content.Context
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.apmgor.forecast.data.locationprovider.LocationProvider
import ru.apmgor.forecast.data.locationprovider.LocationProviderImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class LocationProviderModule {

    @Binds
    @Singleton
    abstract fun bindLocationProvider(locImpl: LocationProviderImpl): LocationProvider
}

@Module
@InstallIn(SingletonComponent::class)
object LocationProviderProvideModule {

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext appContext: Context) =
        LocationServices.getFusedLocationProviderClient(appContext)

    @Provides
    @Singleton
    fun provideLocationRequest() = LocationRequest.create().apply {
        priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
    }
}




