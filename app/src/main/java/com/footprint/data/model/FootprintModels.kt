package com.footprint.data.model

import java.time.LocalDate

data class FootprintEntry(
    val id: Long = 0,
    val title: String,
    val location: String,
    val detail: String,
    val mood: Mood,
    val tags: List<String>,
    val distanceKm: Double,
    val photos: List<String>,
    val energyLevel: Int,
    val happenedOn: LocalDate
)

data class TravelGoal(
    val id: Long = 0,
    val title: String,
    val targetLocation: String,
    val targetDate: LocalDate,
    val notes: String,
    val isCompleted: Boolean,
    val progress: Int
)

data class PeriodStats(
    val totalEntries: Int = 0,
    val totalDistance: Double = 0.0,
    val dominantMood: Mood? = null,
    val uniquePlaces: Int = 0,
    val energyAverage: Double = 0.0
)

data class FootprintSummary(
    val yearly: PeriodStats = PeriodStats(),
    val monthly: PeriodStats = PeriodStats(),
    val daysActiveThisYear: Int = 0,
    val streakDays: Int = 0
)
