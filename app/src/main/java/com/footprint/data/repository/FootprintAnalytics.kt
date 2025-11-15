package com.footprint.data.repository

import com.footprint.data.model.FootprintEntry
import com.footprint.data.model.FootprintSummary
import com.footprint.data.model.Mood
import com.footprint.data.model.PeriodStats
import java.time.LocalDate

object FootprintAnalytics {
    fun buildSummary(entries: List<FootprintEntry>): FootprintSummary {
        val today = LocalDate.now()
        val yearlyEntries = entries.filter { it.happenedOn.year == today.year }
        val monthlyEntries = yearlyEntries.filter { it.happenedOn.month == today.month }

        return FootprintSummary(
            yearly = buildPeriodStats(yearlyEntries),
            monthly = buildPeriodStats(monthlyEntries),
            daysActiveThisYear = yearlyEntries.map { it.happenedOn }.toSet().size,
            streakDays = computeStreak(entries.map { it.happenedOn })
        )
    }

    private fun buildPeriodStats(entries: List<FootprintEntry>): PeriodStats {
        if (entries.isEmpty()) return PeriodStats()
        val totalDistance = entries.sumOf { it.distanceKm }
        val moods = entries.groupingBy { it.mood }.eachCount()
        val dominantMood = moods.maxByOrNull { it.value }?.key
        val uniquePlaces = entries.map { it.location }.toSet().size
        val energyAvg = entries.map { it.energyLevel }.average()
        return PeriodStats(
            totalEntries = entries.size,
            totalDistance = totalDistance,
            dominantMood = dominantMood,
            uniquePlaces = uniquePlaces,
            energyAverage = energyAvg
        )
    }

    private fun computeStreak(dates: List<LocalDate>): Int {
        if (dates.isEmpty()) return 0
        val uniqueDates = dates.toSet()
        var streak = 0
        var cursor = LocalDate.now()
        while (uniqueDates.contains(cursor.minusDays(streak.toLong()))) {
            streak++
        }
        return streak
    }
}
