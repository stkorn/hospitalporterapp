package com.ekstkorn.hospitalporterapp.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BuildingEntity(
        @PrimaryKey
        val buildingId: String,
        val buildingName: String
)
