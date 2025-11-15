package com.footprint.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.footprint.data.model.Mood
import java.time.LocalDate

@Entity(tableName = "footprints")
data class FootprintEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val location: String,
    val detail: String,
    val mood: Mood,
    val tags: List<String>,
    @ColumnInfo(name = "distance_km") val distanceKm: Double,
    @ColumnInfo(name = "photos") val photos: List<String>,
    @ColumnInfo(name = "energy_level") val energyLevel: Int,
    @ColumnInfo(name = "happened_on") val happenedOn: LocalDate
)
