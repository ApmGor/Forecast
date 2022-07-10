package ru.apmgor.forecast.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ForecastDAO {
    @Transaction
    @Query("SELECT * FROM city")
    fun getWeatherForecast(): WeatherForecastEntity?

    @Query("SELECT * FROM city")
    fun getCity(): Flow<CityEntity?>

    @Query("SELECT * FROM timeout")
    fun getTimeout(): TimeoutEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCity(entityForecastCity: CityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveCurrentForecast(entityForecastCurrent: CurrentForecastEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveMinutelyForecast(entityForecastMinutely: List<MinutelyForecastEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveHourlyForecast(entityForecastHourly: List<HourlyForecastEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveDailyForecast(entityForecastDaily: List<DailyForecastEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAlertsForecast(entityForecastAlerts: List<AlertsForecastEntity>)

    @Query("DELETE FROM alerts")
    fun clearAlerts()

    @Query("DELETE FROM minutely")
    fun clearMinutely()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveTimeout(timeout: TimeoutEntity)

    @Transaction
    fun saveForecast(
        entityForecastCity: CityEntity,
        entityForecastCurrent: CurrentForecastEntity,
        entityForecastMinutely: List<MinutelyForecastEntity>,
        entityForecastHourly: List<HourlyForecastEntity>,
        entityForecastDaily: List<DailyForecastEntity>,
        entityForecastAlerts: List<AlertsForecastEntity>
    ) {
        saveCity(entityForecastCity)
        saveCurrentForecast(entityForecastCurrent)
        // Иногда остаются старые данные, если прошлый прогноз имел поминутные данные, а последующий вернул null
        // Поэтому нужно очищать таблицу всегда
        clearMinutely()
        saveMinutelyForecast(entityForecastMinutely)
        saveHourlyForecast(entityForecastHourly)
        saveDailyForecast(entityForecastDaily)
        // Alerts имеет динамический размер в отличии от вышеозначенных,
        // поэтому его лучше очистить, перед заполнением, стратегия замены не всегда сработает
        // Причина из minutely так же актуальна и для этой таблицы
        clearAlerts()
        saveAlertsForecast(entityForecastAlerts)
    }
}