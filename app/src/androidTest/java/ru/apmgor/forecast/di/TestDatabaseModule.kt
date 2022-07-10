package ru.apmgor.forecast.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import ru.apmgor.forecast.data.database.ForecastDatabase
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class]
)
object TestDatabaseModule {

    @Provides
    @Singleton
    fun provideForecastDatabaseTest(@ApplicationContext appContext: Context) =
        Room.inMemoryDatabaseBuilder(
            appContext,
            ForecastDatabase::class.java
        ).build()
}