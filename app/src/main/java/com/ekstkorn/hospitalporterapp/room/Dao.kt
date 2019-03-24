package com.ekstkorn.hospitalporterapp.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface BuildingDao {

    @Query("SELECT COUNT(*) FROM BuildingEntity")
    fun getBuildingCount() : Flowable<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBuilding(entity: List<BuildingEntity>)

    @Query("SELECT * FROM BuildingEntity")
    fun getAllBuilding() : Flowable<List<BuildingEntity>>

    @Query("DELETE FROM BuildingEntity")
    fun deleteAll()
}