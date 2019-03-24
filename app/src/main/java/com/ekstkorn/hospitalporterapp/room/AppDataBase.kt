package com.ekstkorn.hospitalporterapp.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BuildingEntity::class], version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun buildingDao() : BuildingDao

}
