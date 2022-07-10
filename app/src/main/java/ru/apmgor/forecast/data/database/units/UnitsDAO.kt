package ru.apmgor.forecast.data.database.units

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitsDAO {

    @Query("SELECT * FROM units")
    fun getUnits(): Flow<UnitsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUnits(unitsEntity: UnitsEntity)
}