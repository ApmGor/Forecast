package ru.apmgor.forecast.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.apmgor.forecast.data.database.ForecastDatabase
import javax.inject.Singleton

private const val DB_NAME = "forecast_database.db"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideForecastDAO(database: ForecastDatabase) = database.getForecastDAO()

    @Provides
    @Singleton
    fun provideUnitsDAO(database: ForecastDatabase) = database.getUnitsDAO()

    @Provides
    @Singleton
    fun provideForecastDatabase(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(
            appContext,
            ForecastDatabase::class.java,
            DB_NAME
        ).build()
}