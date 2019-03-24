package com.ekstkorn.hospitalporterapp.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ekstkorn.hospitalporterapp.model.Building

@Entity
data class BuildingEntity(
        @PrimaryKey
        val buildingId: String,
        val buildingName: String
)

